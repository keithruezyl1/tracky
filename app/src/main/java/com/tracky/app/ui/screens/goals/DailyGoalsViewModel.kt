package com.tracky.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.repository.GoalRepository
import com.tracky.app.domain.model.DailyGoal
import com.tracky.app.domain.model.MacroDistribution
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyGoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyGoalsUiState())
    val uiState: StateFlow<DailyGoalsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentGoal()
    }

    private fun loadCurrentGoal() {
        viewModelScope.launch {
            val goal = goalRepository.getCurrentGoalOnce()
            if (goal != null) {
                _uiState.update {
                    it.copy(
                        calorieGoal = goal.calorieGoalKcal,
                        carbsPct = goal.carbsPct,
                        proteinPct = goal.proteinPct,
                        fatPct = goal.fatPct
                    )
                }
            }
        }
    }

    fun setCalorieGoal(calories: Int) {
        _uiState.update { 
            val newState = it.copy(calorieGoal = calories)
            newState.copy(
                carbsG = calculateCarbsG(calories, it.carbsPct),
                proteinG = calculateProteinG(calories, it.proteinPct),
                fatG = calculateFatG(calories, it.fatPct)
            )
        }
    }

    fun setCarbsPct(pct: Int) {
        _uiState.update { 
            val newState = it.copy(carbsPct = pct.coerceIn(0, 100))
            newState.copy(carbsG = calculateCarbsG(it.calorieGoal, pct))
        }
    }

    fun setProteinPct(pct: Int) {
        _uiState.update { 
            val newState = it.copy(proteinPct = pct.coerceIn(0, 100))
            newState.copy(proteinG = calculateProteinG(it.calorieGoal, pct))
        }
    }

    fun setFatPct(pct: Int) {
        _uiState.update { 
            val newState = it.copy(fatPct = pct.coerceIn(0, 100))
            newState.copy(fatG = calculateFatG(it.calorieGoal, pct))
        }
    }

    fun saveGoals() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isValid) {
                goalRepository.saveGoal(
                    calorieGoalKcal = state.calorieGoal,
                    macroDistribution = MacroDistribution(
                        carbsPct = state.carbsPct,
                        proteinPct = state.proteinPct,
                        fatPct = state.fatPct
                    )
                )
            }
        }
    }

    private fun calculateCarbsG(calories: Int, pct: Int): Float {
        return (calories * pct / 100f) / 4f
    }

    private fun calculateProteinG(calories: Int, pct: Int): Float {
        return (calories * pct / 100f) / 4f
    }

    private fun calculateFatG(calories: Int, pct: Int): Float {
        return (calories * pct / 100f) / 9f
    }
}

data class DailyGoalsUiState(
    val calorieGoal: Int = 2000,
    val carbsPct: Int = 50,
    val proteinPct: Int = 25,
    val fatPct: Int = 25,
    val carbsG: Float = 250f,
    val proteinG: Float = 125f,
    val fatG: Float = 56f
) {
    val total: Int
        get() = carbsPct + proteinPct + fatPct

    val isValid: Boolean
        get() = total == 100 && calorieGoal > 0
}
