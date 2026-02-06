package com.tracky.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.repository.GoalRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.WeightRepository
import com.tracky.app.domain.model.MacroDistribution
import com.tracky.app.domain.model.UnitPreference
import com.tracky.app.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val weightRepository: WeightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Step 1: Units Selection
    // ─────────────────────────────────────────────────────────────────────────

    fun setUnitPreference(preference: UnitPreference) {
        _uiState.update { it.copy(unitPreference = preference) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 2: Profile (Height, Weight, Target)
    // ─────────────────────────────────────────────────────────────────────────

    fun setHeightCm(height: Float) {
        _uiState.update { state ->
            val bmi = if (state.currentWeightKg > 0 && height > 0) {
                UserProfile.calculateBmi(state.currentWeightKg, height)
            } else 0f
            state.copy(heightCm = height, bmi = bmi)
        }
    }

    fun setCurrentWeightKg(weight: Float) {
        _uiState.update { state ->
            val bmi = if (weight > 0 && state.heightCm > 0) {
                UserProfile.calculateBmi(weight, state.heightCm)
            } else 0f
            state.copy(currentWeightKg = weight, bmi = bmi)
        }
    }

    fun setTargetWeightKg(weight: Float) {
        _uiState.update { it.copy(targetWeightKg = weight) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 3: Daily Goals
    // ─────────────────────────────────────────────────────────────────────────

    fun setCalorieGoal(calories: Float) {
        _uiState.update { it.copy(calorieGoalKcal = calories) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 4: Macro Distribution
    // ─────────────────────────────────────────────────────────────────────────

    fun setCarbsPct(pct: Int) {
        _uiState.update { state ->
            val newDistribution = state.macroDistribution.copy(carbsPct = pct.coerceIn(0, 100))
            state.copy(macroDistribution = newDistribution)
        }
    }

    fun setProteinPct(pct: Int) {
        _uiState.update { state ->
            val newDistribution = state.macroDistribution.copy(proteinPct = pct.coerceIn(0, 100))
            state.copy(macroDistribution = newDistribution)
        }
    }

    fun setFatPct(pct: Int) {
        _uiState.update { state ->
            val newDistribution = state.macroDistribution.copy(fatPct = pct.coerceIn(0, 100))
            state.copy(macroDistribution = newDistribution)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────────────────

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep < OnboardingStep.entries.size - 1) {
            _uiState.update { it.copy(currentStep = currentStep + 1) }
        }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 0) {
            _uiState.update { it.copy(currentStep = currentStep - 1) }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Completion
    // ─────────────────────────────────────────────────────────────────────────

    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val state = _uiState.value
                val now = System.currentTimeMillis()

                // Validate macro distribution
                if (!state.macroDistribution.isValid) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Macro percentages must sum to 100"
                        ) 
                    }
                    return@launch
                }

                // Save profile
                val profile = UserProfile(
                    heightCm = state.heightCm,
                    currentWeightKg = state.currentWeightKg,
                    targetWeightKg = state.targetWeightKg,
                    unitPreference = state.unitPreference,
                    timezone = TimeZone.getDefault().id,
                    bmi = state.bmi,
                    createdAt = now,
                    updatedAt = now
                )
                profileRepository.saveProfile(profile)
                profileRepository.setUnitPreference(state.unitPreference)

                // Save initial weight entry
                val today = kotlinx.datetime.Clock.System.now()
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    .date.toString()
                weightRepository.addEntry(state.currentWeightKg, today)

                // Save daily goal
                goalRepository.saveGoal(
                    calorieGoalKcal = state.calorieGoalKcal,
                    macroDistribution = state.macroDistribution
                )

                // Mark onboarding as complete
                profileRepository.setOnboardingCompleted(true)

                _uiState.update { it.copy(isLoading = false, isComplete = true) }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Failed to save data") 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class OnboardingUiState(
    val currentStep: Int = 0,
    
    // Step 1: Units
    val unitPreference: UnitPreference = UnitPreference.METRIC,
    
    // Step 2: Profile
    val heightCm: Float = 0f,
    val currentWeightKg: Float = 0f,
    val targetWeightKg: Float = 0f,
    val bmi: Float = 0f,
    
    // Step 3: Daily Goals
    val calorieGoalKcal: Float = 2000f,
    
    // Step 4: Macro Distribution
    val macroDistribution: MacroDistribution = MacroDistribution(
        carbsPct = 50,
        proteinPct = 25,
        fatPct = 25
    ),
    
    // State
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
) {
    val canProceedFromStep1: Boolean
        get() = true // Units always have a default

    val canProceedFromStep2: Boolean
        get() = heightCm > 0 && currentWeightKg > 0 && targetWeightKg > 0

    val canProceedFromStep3: Boolean
        get() = calorieGoalKcal > 0

    val canProceedFromStep4: Boolean
        get() = macroDistribution.isValid
}

enum class OnboardingStep {
    UNITS,
    PROFILE,
    GOALS,
    MACROS
}
