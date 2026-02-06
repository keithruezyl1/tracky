package com.tracky.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.local.preferences.UserPreferencesDataStore
import com.tracky.app.data.repository.ChatRepository
import com.tracky.app.data.repository.GoalRepository
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.domain.model.ChatMessage
import com.tracky.app.domain.model.DailyGoal
import com.tracky.app.domain.model.DailySummary
import com.tracky.app.domain.model.DraftData
import com.tracky.app.domain.usecase.ConfirmResult
import com.tracky.app.domain.usecase.DraftLoggingInteractor
import com.tracky.app.domain.usecase.DraftState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loggingRepository: LoggingRepository,
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val draftLoggingInteractor: DraftLoggingInteractor,
    private val chatRepository: ChatRepository,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val stripDays = 6

    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val draftState: StateFlow<DraftState> = draftLoggingInteractor.draftState

    // Chat messages for the currently selected date
    val chatMessages: StateFlow<List<ChatMessage>> = _selectedDate
        .flatMapLatest { date ->
            chatRepository.getMessagesForDate(date.toString())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Day strip dates (oldest to newest)
    // Computed dynamically based on selectedDate
    private val _weekDates = MutableStateFlow<List<LocalDate>>(computeWeekDates(getTodayDate()))
    val weekDates: StateFlow<List<LocalDate>> = _weekDates.asStateFlow()

    // Week summaries for calendar status coloring
    private val _weekSummaries = MutableStateFlow<Map<LocalDate, DailySummary?>>(emptyMap())
    val weekSummaries: StateFlow<Map<LocalDate, DailySummary?>> = _weekSummaries.asStateFlow()

    // Current goal for fallback when day-specific goal is null
    val currentGoal: StateFlow<DailyGoal?> = goalRepository.getCurrentGoal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun computeWeekDates(endDate: LocalDate): List<LocalDate> {
        val today = getTodayDate()
        // Cap endDate at today (can't go into future)
        val effectiveEnd = if (endDate > today) today else endDate
        return (0 until stripDays).map { daysAgo ->
            effectiveEnd.minus((stripDays - 1) - daysAgo, DateTimeUnit.DAY)
        }
    }

    // Daily summary flow
    val dailySummary: StateFlow<DailySummary?> = _selectedDate
        .flatMapLatest { date ->
            loggingRepository.getDailySummary(date.toString())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        observeSelectedDateChanges()
        observePhotoPreference()
        loadWeekSummaries()
    }

    private var weekSummariesJob: Job? = null

    private fun loadWeekSummaries() {
        weekSummariesJob?.cancel()
        weekSummariesJob = viewModelScope.launch {
            _weekSummaries.value = emptyMap()
            _weekDates.value.forEach { date ->
                launch {
                    loggingRepository.getDailySummary(date.toString()).collect { summary ->
                        _weekSummaries.update { current ->
                            current + (date to summary)
                        }
                    }
                }
            }
        }
    }

    private fun observeSelectedDateChanges() {
        viewModelScope.launch {
            _selectedDate.collect { date ->
                // Do NOT cancel drafts when changing dates (Persistence Fix)
                loadDailySummary(date)
            }
        }
    }

    private fun loadDailySummary(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            loggingRepository.getDailySummary(date.toString()).collect { summary ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        currentSummary = summary
                    ) 
                }
            }
        }
    }

    private fun observePhotoPreference() {
        viewModelScope.launch {
            preferencesDataStore.storePhotosLocally.collect { store ->
                _uiState.update { it.copy(storePhotosLocally = store) }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectToday() {
        _selectedDate.value = getTodayDate()
        _weekDates.value = computeWeekDates(getTodayDate())
        loadWeekSummaries()
    }

    /**
     * Select a specific date from calendar and update the day strip
     */
    fun selectDateFromCalendar(date: LocalDate) {
        _selectedDate.value = date
        _weekDates.value = computeWeekDates(date)
        loadWeekSummaries()
    }

    /**
     * Select previous day (swipe right)
     * If at leftmost day, shift window back and select the new rightmost day
     */
    fun selectPreviousDay() {
        val dates = _weekDates.value
        val currentIndex = dates.indexOf(_selectedDate.value)
        if (currentIndex > 0) {
            // Move to previous day within current window
            _selectedDate.value = dates[currentIndex - 1]
        } else if (currentIndex == 0) {
            // At leftmost edge - shift window back by one full page (no overlap)
            val newEndDate = dates.first().minus(1, DateTimeUnit.DAY)
            _weekDates.value = computeWeekDates(newEndDate)
            _selectedDate.value = _weekDates.value.last() // Select the new rightmost day
            loadWeekSummaries()
        }
    }

    /**
     * Select next day (swipe left)
     * If at rightmost day and not at today, shift window forward and select the new leftmost day
     */
    fun selectNextDay() {
        val dates = _weekDates.value
        val currentIndex = dates.indexOf(_selectedDate.value)
        val today = getTodayDate()
        
        if (currentIndex >= 0 && currentIndex < dates.size - 1) {
            // Move to next day within current window
            _selectedDate.value = dates[currentIndex + 1]
        } else if (currentIndex == dates.size - 1 && dates.last() < today) {
            // At rightmost edge and not at today - shift window forward by one full page (no overlap)
            val newEndDate = dates.last().plus(stripDays.toLong(), DateTimeUnit.DAY)
            _weekDates.value = computeWeekDates(newEndDate)
            _selectedDate.value = _weekDates.value.first() // Select the new leftmost day
            loadWeekSummaries()
        }
    }

    /**
     * Delete a food entry by ID
     */
    fun deleteFoodEntry(entryId: Long) {
        viewModelScope.launch {
            loggingRepository.deleteFoodEntry(entryId)
            loadDailySummary(_selectedDate.value)
            loadWeekSummaries()
        }
    }

    /**
     * Delete an exercise entry by ID
     */
    fun deleteExerciseEntry(entryId: Long) {
        viewModelScope.launch {
            loggingRepository.deleteExerciseEntry(entryId)
            loadDailySummary(_selectedDate.value)
            loadWeekSummaries()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Food/Exercise Logging
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Log food or exercise using AI auto-detection
     */
    fun logAutoFromText(text: String) {
        viewModelScope.launch {
            // Append user chat message
            val date = _selectedDate.value.toString()
            chatRepository.addUserTextMessage(date, text)

            _uiState.update { it.copy(inputText = "") }
            draftLoggingInteractor.draftAutoFromText(text, _selectedDate.value)
        }
    }

    fun logFoodFromText(text: String) {
        viewModelScope.launch {
            // Append user chat message
            val date = _selectedDate.value.toString()
            chatRepository.addUserTextMessage(date, text)

            _uiState.update { it.copy(inputText = "") }
            draftLoggingInteractor.draftFoodFromText(text, _selectedDate.value)
        }
    }

    fun logAutoFromImage(imageBase64: String) {
        viewModelScope.launch {
            // Respect privacy setting: avoid uploads when storing photos locally.
            if (_uiState.value.storePhotosLocally) {
                _uiState.update {
                    it.copy(
                        error = "Photo was not sent due to your privacy setting."
                    )
                }
            } else {
                draftLoggingInteractor.draftAutoFromImage(imageBase64, _selectedDate.value)
            }
        }
    }

    fun logExerciseFromText(text: String) {
        viewModelScope.launch {
            // Append user chat message
            val date = _selectedDate.value.toString()
            chatRepository.addUserTextMessage(date, text)

            _uiState.update { it.copy(inputText = "") }
            draftLoggingInteractor.draftExerciseFromText(text, _selectedDate.value)
        }
    }

    fun confirmFoodDraft(draft: DraftData.FoodDraft) {
        viewModelScope.launch {
            when (val result = draftLoggingInteractor.confirmFoodDraft(draft, _selectedDate.value)) {
                is ConfirmResult.Success -> {
                    // Refresh summary and week summaries
                    loadDailySummary(_selectedDate.value)
                    loadWeekSummaries()
                }
                is ConfirmResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    fun confirmExerciseDraft(draft: DraftData.ExerciseDraft) {
        viewModelScope.launch {
            when (val result = draftLoggingInteractor.confirmExerciseDraft(draft, _selectedDate.value)) {
                is ConfirmResult.Success -> {
                    // Refresh summary and week summaries
                    loadDailySummary(_selectedDate.value)
                    loadWeekSummaries()
                }
                is ConfirmResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    fun cancelDraft(draftId: Long? = null) {
        draftLoggingInteractor.cancelDraft(draftId)
    }

    fun updateFoodDraftItem(draftId: Long, index: Int, name: String, quantity: Double, unit: String) {
        viewModelScope.launch {
            draftLoggingInteractor.updateFoodDraftItem(draftId, index, name, quantity, unit)
        }
    }

    fun updateExerciseDraftItem(draftId: Long, index: Int, activity: String, durationMinutes: Int) {
        viewModelScope.launch {
            draftLoggingInteractor.updateExerciseDraftItem(draftId, index, activity, durationMinutes)
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun setInputMode(mode: InputMode) {
        _uiState.update { it.copy(inputMode = mode) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Delete a chat message by its ID
     */
    fun deleteChatMessage(messageId: Long) {
        viewModelScope.launch {
            chatRepository.deleteMessage(messageId)
        }
    }

    /**
     * Re-analyze a food entry with updated text
     * This will delete the old entry and create a new draft for the user to confirm
     */
    fun reanalyzeFoodEntry(foodEntryId: Long, newText: String) {
        viewModelScope.launch {
            try {
                // Delete the old food entry
                loggingRepository.deleteFoodEntry(foodEntryId)
                
                // Add user message for the re-analysis
                val date = _selectedDate.value.toString()
                chatRepository.addUserTextMessage(date, "Re-analyzed: $newText")
                
                // Start drafting with the new text
                draftLoggingInteractor.draftFoodFromText(newText, _selectedDate.value)
                
                // Refresh the summary
                loadDailySummary(_selectedDate.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to re-analyze: ${e.message}") }
            }
        }
    }

    private fun getTodayDate(): LocalDate {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val currentSummary: DailySummary? = null,
    val inputText: String = "",
    val inputMode: InputMode = InputMode.FOOD,
    val error: String? = null,
    val storePhotosLocally: Boolean = true
)

enum class InputMode {
    FOOD,
    EXERCISE
}
