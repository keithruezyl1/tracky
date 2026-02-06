package com.tracky.app.ui.screens.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.WeightRepository
import com.tracky.app.domain.model.WeightChartRange
import com.tracky.app.domain.model.WeightEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class WeightTrackerViewModel @Inject constructor(
    private val weightRepository: WeightRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightTrackerUiState())
    val uiState: StateFlow<WeightTrackerUiState> = _uiState.asStateFlow()

    private val _selectedRange = MutableStateFlow(WeightChartRange.MONTH)
    val selectedRange: StateFlow<WeightChartRange> = _selectedRange.asStateFlow()

    val weightEntries: StateFlow<List<WeightEntry>> = combine(
        _selectedRange,
        weightRepository.getEntriesForRange(_selectedRange.value)
    ) { _, entries ->
        entries
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Observe Profile for Unit, Height, Target changes
            profileRepository.getProfile().collect { profile ->
                if (profile != null) {
                    // Update start weight based on profile creation/update time
                    val targetSetTime = profile.updatedAt
                    // We need to fetch entries to find start weight. 
                    // This might be expensive to do on every profile update if entries are large, 
                    // but profile updates are rare.
                    val entries = weightRepository.getAllEntries().first()
                    val startWeight = entries.minByOrNull { kotlin.math.abs(it.timestamp - targetSetTime) }?.weightKg
                    
                    _uiState.update { state ->
                        state.copy(
                            // Prefer latest entry from repo if available, else profile
                            currentWeightKg = profile.currentWeightKg, 
                            targetWeightKg = profile.targetWeightKg,
                            heightCm = profile.heightCm,
                            unitPreference = profile.unitPreference,
                            startWeightKg = startWeight,
                            isLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
             // Observe entries for selected range
            weightRepository.getEntriesForRange(_selectedRange.value).collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }

    fun updateHeight(heightCm: Float) {
        viewModelScope.launch {
            val profile = profileRepository.getProfileOnce()
            if (profile != null) {
                profileRepository.saveProfile(
                    profile.copy(
                        heightCm = heightCm,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
            // State will update via flow collection
        }
    }

    fun selectRange(range: WeightChartRange) {
        _selectedRange.value = range
        viewModelScope.launch {
            weightRepository.getEntriesForRange(range).collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }

    fun addWeightEntry(weightKg: Float, note: String? = null) {
        viewModelScope.launch {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()
            
            weightRepository.addEntry(weightKg, today, note)
            
            // Update current weight in profile
            val profile = profileRepository.getProfileOnce()
            if (profile != null) {
                profileRepository.updateWeight(weightKg, profile.heightCm)
            }

            _uiState.update { it.copy(currentWeightKg = weightKg) }
        }
    }

    fun updateWeightEntry(entry: WeightEntry) {
        viewModelScope.launch {
            weightRepository.updateEntry(entry)
        }
    }

    fun deleteWeightEntry(id: Long) {
        viewModelScope.launch {
            // Protect the initial weight entry (earliest by timestamp)
            // We fetch all entries to find the global earliest, not just the currently filtered list
            val allEntries = weightRepository.getAllEntries().first()
            val earliestEntry = allEntries.minByOrNull { it.timestamp }
            
            if (earliestEntry != null && earliestEntry.id == id) {
                // User requirement: "should NOT be able to delete/remove the initial weight entry"
                _uiState.update { it.copy(error = "Cannot delete the initial weight entry.") }
                return@launch
            }
            
            weightRepository.deleteEntry(id)
            
            // Recalculate current weight from remaining entries
            val remainingEntries = weightRepository.getAllEntries().first()
            val latestEntry = remainingEntries.maxByOrNull { it.timestamp }
            val newEarliestEntry = remainingEntries.minByOrNull { it.timestamp }
            
            if (latestEntry != null) {
                // Update profile's current weight to match latest entry
                val profile = profileRepository.getProfileOnce()
                if (profile != null) {
                    profileRepository.updateWeight(latestEntry.weightKg, profile.heightCm)
                }
                // Update UI state with recalculated values
                _uiState.update { 
                    it.copy(
                        currentWeightKg = latestEntry.weightKg,
                        startWeightKg = newEarliestEntry?.weightKg
                    ) 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showEditDialog(entry: WeightEntry) {
        _uiState.update { it.copy(editingEntry = entry) }
    }

    fun hideEditDialog() {
        _uiState.update { it.copy(editingEntry = null) }
    }

    fun showEditCurrentWeightDialog() {
        _uiState.update { it.copy(showEditCurrentWeight = true) }
    }

    fun hideEditCurrentWeightDialog() {
        _uiState.update { it.copy(showEditCurrentWeight = false) }
    }

    fun showEditTargetWeightDialog() {
        _uiState.update { it.copy(showEditTargetWeight = true) }
    }

    fun hideEditTargetWeightDialog() {
        _uiState.update { it.copy(showEditTargetWeight = false) }
    }

    fun showEditHeightDialog() {
        _uiState.update { it.copy(showEditHeight = true) }
    }

    fun hideEditHeightDialog() {
        _uiState.update { it.copy(showEditHeight = false) }
    }

    fun updateCurrentWeight(weightKg: Float) {
        viewModelScope.launch {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()
            
            // Add as a new weight entry
            weightRepository.addEntry(weightKg, today)
            
            // Update profile
            val profile = profileRepository.getProfileOnce()
            if (profile != null) {
                profileRepository.updateWeight(weightKg, profile.heightCm)
            }

            _uiState.update { it.copy(currentWeightKg = weightKg, showEditCurrentWeight = false) }
        }
    }

    fun updateTargetWeight(weightKg: Float) {
        viewModelScope.launch {
            val profile = profileRepository.getProfileOnce()
            if (profile != null) {
                profileRepository.saveProfile(
                    profile.copy(
                        targetWeightKg = weightKg,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            _uiState.update { it.copy(targetWeightKg = weightKg, showEditTargetWeight = false) }
        }
    }

    fun showWeightSelectionDialog() {
        _uiState.update { it.copy(showWeightSelectionDialog = true) }
    }

    fun hideWeightSelectionDialog() {
        _uiState.update { it.copy(showWeightSelectionDialog = false) }
    }

    fun showEntryOptions(entry: WeightEntry) {
        _uiState.update { it.copy(selectedEntryForOptions = entry, showEntryOptionsDialog = true) }
    }

    fun hideEntryOptions() {
        _uiState.update { it.copy(selectedEntryForOptions = null, showEntryOptionsDialog = false) }
    }
}

data class WeightTrackerUiState(
    val isLoading: Boolean = true,
    val currentWeightKg: Float = 0f,
    val targetWeightKg: Float = 0f,
    val heightCm: Float = 0f,
    val startWeightKg: Float? = null,
    val unitPreference: com.tracky.app.domain.model.UnitPreference = com.tracky.app.domain.model.UnitPreference.METRIC,
    val entries: List<WeightEntry> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingEntry: WeightEntry? = null,
    val showEditCurrentWeight: Boolean = false,
    val showEditTargetWeight: Boolean = false,
    val showEditHeight: Boolean = false,
    val showWeightSelectionDialog: Boolean = false,
    val selectedEntryForOptions: WeightEntry? = null,
    val showEntryOptionsDialog: Boolean = false,
    val error: String? = null
)
