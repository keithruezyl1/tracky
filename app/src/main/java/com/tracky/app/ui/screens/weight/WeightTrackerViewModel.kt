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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import javax.inject.Inject

@HiltViewModel
class WeightTrackerViewModel @Inject constructor(
    private val weightRepository: WeightRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // Chart Calculation Constants
    private val CHART_Y_AXIS_TICKS_COUNT = 5
    private val CHART_Y_AXIS_BUFFER_PERCENT = 0.1f // 10%


    private val _uiState = MutableStateFlow(WeightTrackerUiState())
    val uiState: StateFlow<WeightTrackerUiState> = _uiState.asStateFlow()

    private val _selectedRange = MutableStateFlow(WeightChartRange.MONTH)
    val selectedRange: StateFlow<WeightChartRange> = _selectedRange.asStateFlow()

    // weightEntries flow is removed as we manualy update state in collectors to ensure synchronization


    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            profileRepository.getProfile().collect { profile ->
                if (profile != null) {
                    val targetSetTime = profile.updatedAt
                    val entries = weightRepository.getAllEntries().first()
                    val startWeight = entries.minByOrNull { kotlin.math.abs(it.timestamp - targetSetTime) }?.weightKg
                    
                    _uiState.update { state ->
                        state.copy(
                            currentWeightKg = profile.currentWeightKg, 
                            targetWeightKg = profile.targetWeightKg,
                            heightCm = profile.heightCm,
                            unitPreference = profile.unitPreference,
                            startWeightKg = startWeight,
                            initialEntryId = entries.minByOrNull { it.timestamp }?.id,
                            isLoading = false
                        )
                    }
                }
            }
        }

        // Fix: Decouple List from Chart Filter.
        // We fetch ALL entries for the "Recent History" list so it never hides data.
        // We filter specific ranges for the Chart generation in-memory.
        viewModelScope.launch {
            combine(
                weightRepository.getAllEntries(),
                _selectedRange
            ) { allEntries, range ->
                // 1. List Data: Show all entries, sorted Oldest to Newest
                val historyList = allEntries.sortedBy { it.timestamp }

                // 2. Chart Data: Filter based on User's "Granularity" definitions
                // Day Tab -> Last 7 Days
                // Week Tab -> Last 4 Weeks
                // Month Tab -> Last 6 Months
                // All Tab -> All Time
                
                val now = Clock.System.now()
                val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
                
                val filteredForChart = when (range) {
                    WeightChartRange.DAY -> {
                         val start = now.minus(7, kotlinx.datetime.DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                         allEntries.filter { it.timestamp >= start.toEpochMilliseconds() }
                    }
                    WeightChartRange.WEEK -> {
                        val start = now.minus(28, kotlinx.datetime.DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                        allEntries.filter { it.timestamp >= start.toEpochMilliseconds() }
                    }
                    WeightChartRange.MONTH -> {
                        val start = now.minus(6, kotlinx.datetime.DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
                        allEntries.filter { it.timestamp >= start.toEpochMilliseconds() }
                    }
                    WeightChartRange.ALL -> allEntries
                }

                Triple(historyList, filteredForChart, range)
            }.collect { (history, chartEntries, range) ->
                val chartState = calculateChartState(chartEntries, range)
                _uiState.update { it.copy(entries = history, chartState = chartState) }
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
        }
    }

    fun selectRange(range: WeightChartRange) {
        _selectedRange.value = range
        // No need to launch separate job; the flatMapLatest block above handles it.
    }

    fun addWeightEntry(weightKg: Float, note: String? = null) {
        viewModelScope.launch {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()
            
            val newId = weightRepository.addEntry(weightKg, today, note)
            
            // Update current weight in profile
            val profile = profileRepository.getProfileOnce()
            if (profile != null) {
                profileRepository.updateWeight(weightKg, profile.heightCm)
            }

            // Refresh initial ID in case this was the first entry (unlikely if profile existed, but good practice)
            // Or if we need to re-evaluate what is "initial" (though initial should be oldest)
            // Ideally we re-fetch all entries to support correct state logic if needed, 
            // but for now relying on loadData's initial fetch + updates might be enough if we only care about the *absolute* oldest which shouldn't change on ADD unless backdating.
            // But let's keep it simple.
            
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
                        startWeightKg = newEarliestEntry?.weightKg,
                        initialEntryId = newEarliestEntry?.id
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
    private fun calculateChartState(entries: List<WeightEntry>, range: WeightChartRange): com.tracky.app.domain.model.WeightChartState {
        // Helper to format timestamps
        fun format(ts: Long, pattern: String): String {
            return java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault()).format(java.util.Date(ts))
        }

        val rawPoints = when (range) {
            WeightChartRange.DAY -> {
                // Granularity: Daily (Last 7 Days)
                // Group by Date string to average multiple entries per day
                entries.groupBy { format(it.timestamp, "yyyy-MM-dd") }
                    .map { (_, dailyEntries) ->
                        val avgWeight = dailyEntries.map { it.weightKg }.average().toFloat()
                        val timestamp = dailyEntries.first().timestamp
                        val label = format(timestamp, "EEE") // Mon, Tue...
                        Triple(timestamp, avgWeight, label)
                    }
                    .sortedBy { it.first }
            }
            WeightChartRange.WEEK -> {
                // Granularity: Weekly (Last 4 Weeks)
                // Group by Week (using Calendar or simple math)
                // Simple approx: Group by ISO Week or similar. 
                // Reliable approach: Calendar week of year
                entries.groupBy { entry ->
                    val cal = java.util.Calendar.getInstance()
                    cal.timeInMillis = entry.timestamp
                    "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.WEEK_OF_YEAR)}"
                }.map { (_, weekEntries) ->
                    val avgWeight = weekEntries.map { it.weightKg }.average().toFloat()
                    val firstEntry = weekEntries.minByOrNull { it.timestamp }!!
                    val label = format(firstEntry.timestamp, "MMM d") // Feb 10
                    Triple(firstEntry.timestamp, avgWeight, label)
                }.sortedBy { it.first }
            }
            WeightChartRange.MONTH, WeightChartRange.ALL -> {
                // Granularity: Monthly
                entries.groupBy { format(it.timestamp, "yyyy-MM") }
                    .map { (_, monthEntries) ->
                        val avgWeight = monthEntries.map { it.weightKg }.average().toFloat()
                        val firstEntry = monthEntries.minByOrNull { it.timestamp }!!
                        val label = format(firstEntry.timestamp, "MMM") // Jan, Feb
                        Triple(firstEntry.timestamp, avgWeight, label)
                    }
                    .sortedBy { it.first }
            }
        }

        // Handle empty state for DAY specifically if needed, but with 7-day window we might have data.
        // If truly empty, we can fallback or show empty.
        // Keeping fallback logic for robustness if list is empty but currentWeight exists.
        val finalPointsList = if (rawPoints.isEmpty() && range == WeightChartRange.DAY && _uiState.value.currentWeightKg > 0) {
             listOf(Triple(Clock.System.now().toEpochMilliseconds(), _uiState.value.currentWeightKg, "Today"))
        } else {
            rawPoints
        }

        // 2. Y-Axis Calculation
        val yMin = 0f
        val maxYValue = finalPointsList.maxOfOrNull { it.second } ?: 100f
        val yMax = if (finalPointsList.isEmpty()) 100f else maxYValue * (1f + CHART_Y_AXIS_BUFFER_PERCENT)
        
        val yRange = yMax - yMin
        val interval = yRange / (CHART_Y_AXIS_TICKS_COUNT - 1)
        val yTicks = (0 until CHART_Y_AXIS_TICKS_COUNT).map { i ->
            yMin + (interval * i)
        }

        // 3. Convert to Chart Points
        val finalPoints = finalPointsList.mapIndexed { index, (ts, weight, label) ->
            com.tracky.app.domain.model.WeightChartPoint(
                x = index.toFloat(),
                y = weight,
                timestamp = ts,
                label = label 
            )
        }

        val maxX = if (finalPoints.isNotEmpty()) {
             (finalPoints.size - 1).toFloat()
        } else {
            0f 
        }

        return com.tracky.app.domain.model.WeightChartState(
            points = finalPoints,
            minY = yMin,
            maxY = yMax,
            yAxisTicks = yTicks,
            minTimestamp = 0L, 
            maxX = maxX,
            xAxisTicks = emptyList()
        ) 
    }
}

data class WeightTrackerUiState(
    val isLoading: Boolean = true,
    val currentWeightKg: Float = 0f,
    val targetWeightKg: Float = 0f,
    val heightCm: Float = 0f,
    val startWeightKg: Float? = null,
    val initialEntryId: Long? = null,
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
    val error: String? = null,
    val chartState: com.tracky.app.domain.model.WeightChartState = com.tracky.app.domain.model.WeightChartState()
)
