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
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.ExerciseEntry
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loggingRepository: LoggingRepository,
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val draftLoggingInteractor: DraftLoggingInteractor,
    private val chatRepository: ChatRepository,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val streakInteractor: com.tracky.app.domain.usecase.StreakInteractor,
    private val soundManager: com.tracky.app.ui.sound.SoundManager,
    private val hapticManager: com.tracky.app.ui.haptics.HapticManager
) : ViewModel() {

    private val stripDays = 6

    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Day strip dates (oldest to newest)
    private val _weekDates = MutableStateFlow<List<LocalDate>>(computeWeekDates(getTodayDate()))
    val weekDates: StateFlow<List<LocalDate>> = _weekDates.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    // We'll keep the base UI state for simpler properties, but summaries will be reactive.
    
    val showSuccessOverlay = MutableStateFlow(false)

    private var backupFoodEntry: FoodEntry? = null
    private var backupExerciseEntry: ExerciseEntry? = null
    private var isReanalyzing = false

    private fun sanitizeInput(text: String): String {
        return text.trim().replace("\\s+".toRegex(), " ")
    }

    fun dismissSuccessOverlay() {
        showSuccessOverlay.value = false
    }

    val draftState: StateFlow<DraftState> = combine(
        draftLoggingInteractor.draftState,
        _selectedDate
    ) { draft, date ->
        when (draft) {
            is DraftState.Drafting -> if (draft.date == date) draft else DraftState.Idle
            is DraftState.FoodDraft -> if (draft.data.date == date) draft else DraftState.Idle
            is DraftState.ExerciseDraft -> if (draft.data.date == date) draft else DraftState.Idle
            else -> draft
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DraftState.Idle)

    // Draft states for all dates in the current strip (for animation pinning)
    val weekDraftStates: StateFlow<Map<LocalDate, DraftState>> = combine(
        draftLoggingInteractor.draftState,
        _weekDates
    ) { draft, dates ->
        dates.associateWith { date ->
            when (draft) {
                is DraftState.Drafting -> if (draft.date == date) draft else DraftState.Idle
                is DraftState.FoodDraft -> if (draft.data.date == date) draft else DraftState.Idle
                is DraftState.ExerciseDraft -> if (draft.data.date == date) draft else DraftState.Idle
                else -> DraftState.Idle
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Chat messages for the currently selected date
    val chatMessages: StateFlow<List<ChatMessage>> = _selectedDate
        .flatMapLatest { date ->
            chatRepository.getMessagesForDate(date.toString())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All chat messages for dates in the current strip
    val weekChatMessages: StateFlow<Map<LocalDate, List<ChatMessage>>> = _weekDates
        .flatMapLatest { dates ->
            val messageFlows = dates.map { date ->
                chatRepository.getMessagesForDate(date.toString()).map { date to it }
            }
            combine(messageFlows) { it.toMap() }
        }
        .catch { emit(emptyMap()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    // Week summaries are now computed reactively below

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

    // Daily summary flow - Unified Single Source of Truth
    val dailySummary: StateFlow<DailySummary?> = _selectedDate
        .flatMapLatest { date ->
            loggingRepository.getDailySummary(date.toString())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Reactive UI State combining all sources
    val uiState: StateFlow<HomeUiState> = combine(
        _uiState,
        dailySummary,
        profileRepository.getProfile()
    ) { baseState, summary, profile ->
        baseState.copy(
            currentSummary = summary,
            userWeightKg = profile?.currentWeightKg ?: 70f
        )
    }
    .catch { e ->
        _uiState.update { it.copy(error = "Sync Error: ${e.message}") }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        observePhotoPreference()
        monitorGlobalDraftState()
        observeHapticsPreference()
        observeStreakState()
        initializeTimezone()
        checkOrphanedBackups()
    }

    private fun observeStreakState() {
        viewModelScope.launch {
            preferencesDataStore.streakStateJson.collect { json ->
                if (json != null) {
                    try {
                        val info = kotlinx.serialization.json.Json.decodeFromString(com.tracky.app.domain.model.StreakInfo.serializer(), json)
                        _uiState.update { it.copy(streakInfo = info) }
                        checkStreakAnimation(info)
                    } catch (e: Exception) {
                        streakInteractor.calculateStreak()
                    }
                } else {
                    streakInteractor.calculateStreak()
                }
            }
        }
    }

    private suspend fun checkStreakAnimation(info: com.tracky.app.domain.model.StreakInfo) {
        val lastCount = preferencesDataStore.streakLastAnimatedCount.first()
        val lastDate = preferencesDataStore.streakLastAnimatedDate.first()
        val today = getTodayDate().toString()

        // Animate only if count increased and status is ACTIVE, and not animated today for this count
        if (info.status == com.tracky.app.domain.model.StreakStatus.ACTIVE && 
            info.count > lastCount && 
            lastDate != today) {
            
            _uiState.update { it.copy(shouldAnimateStreak = true) }
            preferencesDataStore.setStreakLastAnimatedCount(info.count)
            preferencesDataStore.setStreakLastAnimatedDate(today)
        }
    }

    fun onStreakAnimationComplete() {
        _uiState.update { it.copy(shouldAnimateStreak = false) }
    }

    fun showStreakModal() {
        _uiState.update { it.copy(showStreakModal = true) }
    }

    fun dismissStreakModal() {
        _uiState.update { it.copy(showStreakModal = false) }
    }

    private fun checkOrphanedBackups() {
        viewModelScope.launch {
            val prefId = preferencesDataStore.reanalyzingEntryId.first()
            if (prefId != null) {
                isReanalyzing = true
                // Give some time for DraftLoggingInteractor to initialize and potentially start a draft
                kotlinx.coroutines.delay(2000)
                if (draftLoggingInteractor.draftState.value is DraftState.Idle) {
                    restoreBackup()
                }
            }
        }
    }

    private fun initializeTimezone() {
        viewModelScope.launch {
            val existing = preferencesDataStore.homeTimezone.first()
            if (existing == null) {
                val current = TimeZone.currentSystemDefault().id
                preferencesDataStore.setHomeTimezone(current)
            }
        }
    }

    private fun monitorGlobalDraftState() {
        viewModelScope.launch {
            draftLoggingInteractor.draftState.collect { globalState ->
                if (globalState is DraftState.Idle || globalState is DraftState.Drafting) {
                    _uiState.update { it.copy(shouldAnimateDraft = true) }
                }
                if (globalState is DraftState.Error && isReanalyzing) {
                    // Restore backup on error
                    restoreBackup()
                }
            }
        }
    }

    fun onDraftAppeared() {
        if (_uiState.value.shouldAnimateDraft) {
            soundManager.playPop()
            _uiState.update { it.copy(shouldAnimateDraft = false) }
        }
    }

    // Reactive Week Summaries - No more manual jobs or leaks
    val weekSummaries: StateFlow<Map<LocalDate, DailySummary?>> = _weekDates
        .flatMapLatest { dates ->
            val summaryFlows = dates.map { date ->
                loggingRepository.getDailySummary(date.toString()).map { date to it }
            }
            combine(summaryFlows) { it.toMap() }
        }
        .catch { emit(emptyMap()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // loadDailySummary and observeSelectedDateChanges removed. 
    // State is now handled reactively by dailySummary and uiState flows.

    private fun observePhotoPreference() {
        viewModelScope.launch {
            preferencesDataStore.storePhotosLocally.collect { store ->
                _uiState.update { it.copy(storePhotosLocally = store) }
            }
        }
    }

    private fun observeHapticsPreference() {
        viewModelScope.launch {
            preferencesDataStore.hapticsEnabled.collect { enabled ->
                _uiState.update { it.copy(hapticsEnabled = enabled) }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val today = getTodayDate()
        _selectedDate.value = if (date > today) today else date
    }

    fun selectToday() {
        _selectedDate.value = getTodayDate()
        _weekDates.value = computeWeekDates(getTodayDate())
    }

    /**
     * Select a specific date from calendar and update the day strip
     */
    fun selectDateFromCalendar(date: LocalDate) {
        val today = getTodayDate()
        val effectiveDate = if (date > today) today else date
        val newWeekDates = computeWeekDates(effectiveDate)
        _weekDates.value = newWeekDates
        _selectedDate.value = effectiveDate
    }

    /**
     * Select previous day (swipe right)
     * If at leftmost day, shift window back and select the new rightmost day
     */
    fun selectPreviousDay() {
        val dates = _weekDates.value
        val currentIndex = dates.indexOf(_selectedDate.value)
        if (currentIndex > 0) {
            _selectedDate.value = dates[currentIndex - 1]
        } else {
            // Shift window back by a full chunk (6 days)
            val newEndDate = dates.first().minus(1, DateTimeUnit.DAY)
            val newWeekDates = computeWeekDates(newEndDate)
            _weekDates.value = newWeekDates
            _selectedDate.value = newWeekDates.last()
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
            _selectedDate.value = dates[currentIndex + 1]
        } else if (dates.last() < today) {
            // Shift window forward by a full chunk (6 days)
            val newEndDate = dates.last().plus(stripDays.toLong(), DateTimeUnit.DAY)
            val newWeekDates = computeWeekDates(newEndDate)
            _weekDates.value = newWeekDates
            _selectedDate.value = newWeekDates.first()
        }
    }

    /**
     * Delete a food entry by ID
     */
    fun deleteFoodEntry(entryId: Long) {
        viewModelScope.launch {
            loggingRepository.deleteFoodEntry(entryId)
            soundManager.playCrumple()
            hapticManager.vibrateSoft()
        }
    }

    /**
     * Delete an exercise entry by ID
     */
    fun deleteExerciseEntry(entryId: Long) {
        viewModelScope.launch {
            loggingRepository.deleteExerciseEntry(entryId)
            soundManager.playCrumple()
            hapticManager.vibrateSoft()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Food/Exercise Logging
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Log food or exercise using AI auto-detection
     */
    fun logAutoFromText(text: String, reanalyzeId: Long? = null, reanalyzeType: String? = null) {
        val sanitizedText = sanitizeInput(text)
        viewModelScope.launch {
            if (reanalyzeId != null && reanalyzeType != null) {
                isReanalyzing = true
                preferencesDataStore.setReanalyzingState(reanalyzeId, reanalyzeType)
                if (reanalyzeType == "food") {
                    val entry = loggingRepository.getFoodEntryById(reanalyzeId)
                    if (entry != null) {
                        backupFoodEntry = entry
                        loggingRepository.saveFoodBackup(entry)
                        loggingRepository.deleteFoodEntry(reanalyzeId)
                    }
                    backupExerciseEntry = null
                } else {
                    val entry = loggingRepository.getExerciseEntryById(reanalyzeId)
                    if (entry != null) {
                        backupExerciseEntry = entry
                        loggingRepository.saveExerciseBackup(entry)
                        loggingRepository.deleteExerciseEntry(reanalyzeId)
                    }
                    backupFoodEntry = null
                }
            } else {
                isReanalyzing = false
                preferencesDataStore.setReanalyzingState(null, null)
                backupFoodEntry = null
                backupExerciseEntry = null
            }

            // Append user chat message
            val date = _selectedDate.value.toString()
            chatRepository.addUserTextMessage(date, if (isReanalyzing) "Re-analyzing: $sanitizedText" else sanitizedText)

            _uiState.update { it.copy(inputText = "") }
            draftLoggingInteractor.draftAutoFromText(sanitizedText, _selectedDate.value)
        }
    }

    fun logFoodFromText(text: String) {
        val sanitizedText = sanitizeInput(text)
        viewModelScope.launch {
             isReanalyzing = false
             preferencesDataStore.setReanalyzingState(null, null)
             backupFoodEntry = null
             backupExerciseEntry = null
            // Append user chat message
            val date = _selectedDate.value.toString()
            chatRepository.addUserTextMessage(date, sanitizedText)

            _uiState.update { it.copy(inputText = "") }
            draftLoggingInteractor.draftFoodFromText(sanitizedText, _selectedDate.value)
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
        val sanitizedText = sanitizeInput(text)
        viewModelScope.launch {
            // Append user chat message
            val date = _selectedDate.value.toString()
            chatRepository.addUserTextMessage(date, sanitizedText)

            _uiState.update { it.copy(inputText = "") }
            draftLoggingInteractor.draftExerciseFromText(sanitizedText, _selectedDate.value)
        }
    }

    fun confirmFoodDraft(draft: DraftData.FoodDraft) {
        viewModelScope.launch {
            when (val result = draftLoggingInteractor.confirmFoodDraft(draft, _selectedDate.value)) {
                is ConfirmResult.Success -> {
                    soundManager.playDing()
                    hapticManager.vibrateSuccess()
                    showSuccessOverlay.value = true
                    // Refresh summary and week summaries
                    // Clear re-analysis state
                    val id = backupFoodEntry?.id
                    isReanalyzing = false
                    preferencesDataStore.setReanalyzingState(null, null)
                    backupFoodEntry = null
                    backupExerciseEntry = null
                    id?.let { loggingRepository.deleteBackup(it, "food") }
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
                    soundManager.playDing()
                    hapticManager.vibrateSuccess()
                    showSuccessOverlay.value = true
                    // Refresh summary and week summaries
                    // Clear re-analysis state
                    val id = backupExerciseEntry?.id
                    isReanalyzing = false
                    preferencesDataStore.setReanalyzingState(null, null)
                    backupFoodEntry = null
                    backupExerciseEntry = null
                    id?.let { loggingRepository.deleteBackup(it, "exercise") }
                }
                is ConfirmResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    fun cancelDraft(draftId: Long? = null) {
        draftLoggingInteractor.cancelDraft(draftId)
        
        // Restore backup if we were reanalyzing
        if (isReanalyzing) {
            restoreBackup()
        }
    }

    private fun restoreBackup() {
        viewModelScope.launch {
            val prefId = preferencesDataStore.reanalyzingEntryId.first()
            val prefType = preferencesDataStore.reanalyzingEntryType.first()

            if (prefId != null && prefType != null) {
                if (prefType == "food") {
                    val food = backupFoodEntry ?: loggingRepository.getFoodBackup(prefId)
                    if (food != null) {
                        loggingRepository.saveFoodEntry(food)
                        loggingRepository.deleteBackup(prefId, "food")
                    }
                } else {
                    val exercise = backupExerciseEntry ?: loggingRepository.getExerciseBackup(prefId)
                    if (exercise != null) {
                        loggingRepository.saveExerciseEntry(exercise)
                        loggingRepository.deleteBackup(prefId, "exercise")
                    }
                }
            }
            
            isReanalyzing = false
            preferencesDataStore.setReanalyzingState(null, null)
            backupFoodEntry = null
            backupExerciseEntry = null
        }
    }

    fun updateFoodDraftItem(
        draftId: Long, 
        index: Int, 
        name: String, 
        quantity: Double, 
        unit: String,
        calories: Float,
        carbs: Float,
        protein: Float,
        fat: Float,
        isManual: Boolean
    ) {
        viewModelScope.launch {
            draftLoggingInteractor.updateFoodDraftItem(
                draftId, index, name, quantity, unit, 
                calories, carbs, protein, fat, isManual
            )
        }
    }

    fun updateExerciseDraftItem(
        draftId: Long, 
        index: Int, 
        activity: String, 
        durationMinutes: Int, 
        intensity: com.tracky.app.domain.model.ExerciseIntensity,
        calories: Float,
        isManual: Boolean
    ) {
        viewModelScope.launch {
            draftLoggingInteractor.updateExerciseDraftItem(draftId, index, activity, durationMinutes, intensity, calories, isManual)
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun setInputMode(mode: InputMode) {
        _uiState.update { it.copy(inputMode = mode) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null, isOffline = false) }
    }

    fun dismissOfflineOverlay() {
        _uiState.update { it.copy(isOffline = false) }
    }

    fun toggleOffline(isOffline: Boolean) {
        _uiState.update { it.copy(isOffline = isOffline) }
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
        logAutoFromText(sanitizeInput(newText), foodEntryId, "food")
    }

    fun reanalyzeExerciseEntry(exerciseId: Long, newText: String) {
        logAutoFromText(sanitizeInput(newText), exerciseId, "exercise")
    }

    fun toggleSidebar() {
        _uiState.update { it.copy(showSidebar = !it.showSidebar) }
    }

    fun addToDraft(text: String, isFood: Boolean) {
        val sanitizedText = sanitizeInput(text)
        viewModelScope.launch {
            if (isFood) {
                draftLoggingInteractor.addToFoodDraft(sanitizedText)
            } else {
                draftLoggingInteractor.addToExerciseDraft(sanitizedText)
            }
        }
    }

    fun addToDraftImage(imageBase64: String, isFood: Boolean) {
        viewModelScope.launch {
            if (isFood) {
                draftLoggingInteractor.addToFoodDraftFromImage(imageBase64)
            } else {
                draftLoggingInteractor.addToExerciseDraftFromImage(imageBase64)
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
    val storePhotosLocally: Boolean = true,
    val shouldAnimateDraft: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val userWeightKg: Float = 70f,
    val streakInfo: com.tracky.app.domain.model.StreakInfo = com.tracky.app.domain.model.StreakInfo(),
    val shouldAnimateStreak: Boolean = false,
    val showStreakModal: Boolean = false,
    val showSidebar: Boolean = false,
    val isOffline: Boolean = false
)

enum class InputMode {
    FOOD,
    EXERCISE
}
