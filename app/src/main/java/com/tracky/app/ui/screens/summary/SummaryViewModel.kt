package com.tracky.app.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.WeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

// @HiltViewModel
class SummaryViewModel @Inject constructor(
    private val weightRepository: WeightRepository,
    private val loggingRepository: LoggingRepository
) : ViewModel() {

    private val _timeframe = MutableStateFlow(SummaryTimeframe.WEEK)
    val timeframe = _timeframe.asStateFlow()

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // observeData()
    }

    /*
    private fun observeData() {
        viewModelScope.launch {
            timeframe.collectLatest { tf ->
                _uiState.update { it.copy(isLoading = true) }
                loadSummaryData(tf)
            }
        }
    }

    private suspend fun loadSummaryData(tf: SummaryTimeframe) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = today.minus(tf.days - 1, DateTimeUnit.DAY)
        val startDateStr = startDate.toString()
        val endDateStr = today.toString()

        combine(
            loggingRepository.getTotalFoodCaloriesBetween(startDateStr, endDateStr),
            loggingRepository.getTotalExerciseCaloriesBetween(startDateStr, endDateStr),
            loggingRepository.getMacroTotalsBetween(startDateStr, endDateStr),
            weightRepository.getAllEntries()
        ) { foodCals, exerciseCals, macros, weightEntries ->
            
            // Weight Logic: Find first and last entry in range
            val rangeWeights = weightEntries.filter { 
                val d = try { LocalDate.parse(it.date) } catch (e: Exception) { null }
                d != null && d >= startDate && d <= today 
            }.sortedBy { it.date } // Oldest first

            val startWeight = rangeWeights.firstOrNull()?.weightKg
            val endWeight = rangeWeights.lastOrNull()?.weightKg
            val weightChange = if (startWeight != null && endWeight != null) endWeight - startWeight else 0f

            val days = tf.days
            val avgCals = (foodCals.toFloat() / days).roundToInt()
            val avgCarbs = (macros?.carbs ?: 0f) / days
            val avgProtein = (macros?.protein ?: 0f) / days
            val avgFat = (macros?.fat ?: 0f) / days

            SummaryUiState(
                isLoading = false,
                averageCalories = avgCals,
                totalCalories = foodCals,
                averageCarbs = avgCarbs,
                averageProtein = avgProtein,
                averageFat = avgFat,
                startWeight = startWeight,
                endWeight = endWeight,
                weightChange = weightChange,
                startDate = startDate,
                endDate = today
            )
        }.collectLatest { newState ->
            _uiState.value = newState
        }
    }
    */

    fun setTimeframe(newTimeframe: SummaryTimeframe) {
        _timeframe.value = newTimeframe
    }
}

enum class SummaryTimeframe(val label: String, val days: Int) {
    WEEK("Week", 7),
    TWO_WEEKS("2 Weeks", 14),
    MONTH("Month", 30)
}

data class SummaryUiState(
    val isLoading: Boolean = false,
    val averageCalories: Int = 0,
    val totalCalories: Int = 0,
    val averageCarbs: Float = 0f,
    val averageProtein: Float = 0f,
    val averageFat: Float = 0f,
    val startWeight: Float? = null,
    val endWeight: Float? = null,
    val weightChange: Float = 0f,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)
