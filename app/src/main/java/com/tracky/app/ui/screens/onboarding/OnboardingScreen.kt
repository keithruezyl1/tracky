package com.tracky.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.BmiClassification
import com.tracky.app.domain.model.UnitPreference
import com.tracky.app.domain.model.UserProfile
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.TrackyButtonPrimary
import com.tracky.app.ui.components.TrackyButtonSecondary
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyInfoCard
import com.tracky.app.ui.components.TrackyNumberInput
import com.tracky.app.ui.components.TrackyScreenTitle
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onOnboardingComplete()
        }
    }

    var showMacroSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrackyColors.Background)
            .statusBarsPadding()
            .padding(TrackyTokens.Spacing.ScreenPadding)
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
        ) {
            OnboardingStep.entries.forEachIndexed { index, _ ->
                val isActive = index <= uiState.currentStep
                TrackyChip(
                    label = "${index + 1}",
                    selected = isActive,
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    compact = true
                )
            }
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

        // Step content
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() + scaleIn(initialScale = 0.98f) togetherWith
                    slideOutHorizontally { -it } + fadeOut() + scaleOut(targetScale = 0.98f)
                } else {
                    slideInHorizontally { -it } + fadeIn() + scaleIn(initialScale = 0.98f) togetherWith
                    slideOutHorizontally { it } + fadeOut() + scaleOut(targetScale = 0.98f)
                }
            },
            modifier = Modifier.weight(1f),
            label = "onboarding_step"
        ) { step ->
            when (OnboardingStep.entries[step]) {
                OnboardingStep.UNITS -> UnitsStep(
                    unitPreference = uiState.unitPreference,
                    onUnitSelected = viewModel::setUnitPreference
                )
                OnboardingStep.PROFILE -> ProfileStep(
                    heightCm = uiState.heightCm,
                    currentWeightKg = uiState.currentWeightKg,
                    targetWeightKg = uiState.targetWeightKg,
                    bmi = uiState.bmi,
                    unitPreference = uiState.unitPreference,
                    onHeightChanged = viewModel::setHeightCm,
                    onCurrentWeightChanged = viewModel::setCurrentWeightKg,
                    onTargetWeightChanged = viewModel::setTargetWeightKg
                )
                OnboardingStep.GOALS -> GoalsStep(
                    calorieGoal = uiState.calorieGoalKcal,
                    onCalorieGoalChanged = viewModel::setCalorieGoal
                )
                OnboardingStep.MACROS -> MacrosStep(
                    macroDistribution = uiState.macroDistribution,
                    calorieGoal = uiState.calorieGoalKcal,
                    onEditMacros = { showMacroSheet = true }
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            if (uiState.currentStep > 0) {
                TrackyButtonSecondary(
                    text = "Back",
                    onClick = viewModel::previousStep,
                    modifier = Modifier.weight(1f)
                )
            }

            val canProceed = when (OnboardingStep.entries[uiState.currentStep]) {
                OnboardingStep.UNITS -> uiState.canProceedFromStep1
                OnboardingStep.PROFILE -> uiState.canProceedFromStep2
                OnboardingStep.GOALS -> uiState.canProceedFromStep3
                OnboardingStep.MACROS -> uiState.canProceedFromStep4
            }

            val isLastStep = uiState.currentStep == OnboardingStep.entries.size - 1

            TrackyButtonPrimary(
                text = if (isLastStep) "Finish" else "Continue",
                onClick = {
                    if (isLastStep) {
                        viewModel.completeOnboarding()
                    } else {
                        viewModel.nextStep()
                    }
                },
                enabled = canProceed && !uiState.isLoading,
                modifier = Modifier.weight(if (uiState.currentStep > 0) 1f else 2f)
            )
        }
    }

    // Macro distribution bottom sheet
    if (showMacroSheet) {
        MacroDistributionSheet(
            carbsPct = uiState.macroDistribution.carbsPct,
            proteinPct = uiState.macroDistribution.proteinPct,
            fatPct = uiState.macroDistribution.fatPct,
            onCarbsChanged = viewModel::setCarbsPct,
            onProteinChanged = viewModel::setProteinPct,
            onFatChanged = viewModel::setFatPct,
            onDismiss = { showMacroSheet = false }
        )
    }
}

@Composable
private fun UnitsStep(
    unitPreference: UnitPreference,
    onUnitSelected: (UnitPreference) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TrackyScreenTitle(text = "Select Units")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyBodySmall(
            text = "Choose your preferred measurement system",
            color = TrackyColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

        TrackyCard(
            onClick = { onUnitSelected(UnitPreference.METRIC) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TrackyBodyText(text = "Metric")
                    TrackyBodySmall(
                        text = "kg, cm",
                        color = TrackyColors.TextTertiary
                    )
                }
                TrackyChip(
                    label = if (unitPreference == UnitPreference.METRIC) "Selected" else "Select",
                    selected = unitPreference == UnitPreference.METRIC,
                    onClick = { onUnitSelected(UnitPreference.METRIC) }
                )
            }
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        TrackyCard(
            onClick = { onUnitSelected(UnitPreference.IMPERIAL) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TrackyBodyText(text = "Imperial")
                    TrackyBodySmall(
                        text = "lb, ft/in",
                        color = TrackyColors.TextTertiary
                    )
                }
                TrackyChip(
                    label = if (unitPreference == UnitPreference.IMPERIAL) "Selected" else "Select",
                    selected = unitPreference == UnitPreference.IMPERIAL,
                    onClick = { onUnitSelected(UnitPreference.IMPERIAL) }
                )
            }
        }
    }
}

@Composable
private fun ProfileStep(
    heightCm: Float,
    currentWeightKg: Float,
    targetWeightKg: Float,
    bmi: Float,
    unitPreference: UnitPreference,
    onHeightChanged: (Float) -> Unit,
    onCurrentWeightChanged: (Float) -> Unit,
    onTargetWeightChanged: (Float) -> Unit
) {
    val heightSuffix = if (unitPreference == UnitPreference.METRIC) "cm" else "in"
    val weightSuffix = if (unitPreference == UnitPreference.METRIC) "kg" else "lb"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TrackyScreenTitle(text = "Your Profile")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyBodySmall(
            text = "Enter your body measurements",
            color = TrackyColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

        TrackyNumberInput(
            value = if (heightCm > 0) heightCm.toString() else "",
            onValueChange = { onHeightChanged(it.toFloatOrNull() ?: 0f) },
            label = "Height",
            placeholder = "Enter height",
            suffix = heightSuffix
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        TrackyNumberInput(
            value = if (currentWeightKg > 0) currentWeightKg.toString() else "",
            onValueChange = { onCurrentWeightChanged(it.toFloatOrNull() ?: 0f) },
            label = "Current Weight",
            placeholder = "Enter current weight",
            suffix = weightSuffix
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        TrackyNumberInput(
            value = if (targetWeightKg > 0) targetWeightKg.toString() else "",
            onValueChange = { onTargetWeightChanged(it.toFloatOrNull() ?: 0f) },
            label = "Target Weight",
            placeholder = "Enter target weight",
            suffix = weightSuffix
        )

        if (bmi > 0) {
            val bmiClassification = UserProfile.getBmiClassification(bmi)
            val bmiColor = when (bmiClassification) {
                BmiClassification.UNKNOWN -> TrackyColors.TextTertiary
                BmiClassification.UNDERWEIGHT -> TrackyColors.Warning
                BmiClassification.NORMAL -> TrackyColors.Success
                BmiClassification.OVERWEIGHT -> TrackyColors.Warning
                BmiClassification.OBESE_CLASS_1,
                BmiClassification.OBESE_CLASS_2,
                BmiClassification.OBESE_CLASS_3 -> TrackyColors.Error
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
            TrackyInfoCard(
                containerColor = bmiColor.copy(alpha = 0.1f),
                borderColor = bmiColor.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(text = "Your BMI")
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                        TrackyBodyText(
                            text = String.format("%.1f", bmi),
                            color = bmiColor
                        )
                        TrackyBodySmall(
                            text = bmiClassification.label,
                            color = bmiColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsStep(
    calorieGoal: Int,
    onCalorieGoalChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TrackyScreenTitle(text = "Daily Goals")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyBodySmall(
            text = "Set your daily calorie target",
            color = TrackyColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

        TrackyNumberInput(
            value = calorieGoal.toString(),
            onValueChange = { onCalorieGoalChanged(it.toIntOrNull() ?: 2000) },
            label = "Daily Calorie Goal",
            placeholder = "Enter calorie goal",
            suffix = "kcal",
            allowDecimal = false
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

        // Quick presets
        TrackySectionTitle(text = "Quick Presets")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
        ) {
            listOf(1500, 1800, 2000, 2200, 2500).forEach { preset ->
                TrackyChip(
                    label = "$preset",
                    selected = calorieGoal == preset,
                    onClick = { onCalorieGoalChanged(preset) },
                    modifier = Modifier.weight(1f),
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun MacrosStep(
    macroDistribution: com.tracky.app.domain.model.MacroDistribution,
    calorieGoal: Int,
    onEditMacros: () -> Unit
) {
    val carbsG = (calorieGoal * macroDistribution.carbsPct / 100f) / 4f
    val proteinG = (calorieGoal * macroDistribution.proteinPct / 100f) / 4f
    val fatG = (calorieGoal * macroDistribution.fatPct / 100f) / 9f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TrackyScreenTitle(text = "Macro Distribution")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyBodySmall(
            text = "Set your carbs, protein, and fat percentages",
            color = TrackyColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

        TrackyCard {
            MacroRow("Carbs", macroDistribution.carbsPct, carbsG)
            TrackyDivider()
            MacroRow("Protein", macroDistribution.proteinPct, proteinG)
            TrackyDivider()
            MacroRow("Fat", macroDistribution.fatPct, fatG)
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        val total = macroDistribution.total
        val isValid = macroDistribution.isValid

        if (!isValid) {
            TrackyInfoCard {
                TrackyBodySmall(
                    text = "Total: $total% (must equal 100%)",
                    color = if (total != 100) TrackyColors.Error else TrackyColors.TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        TrackyButtonSecondary(
            text = "Edit Distribution",
            onClick = onEditMacros,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MacroRow(label: String, pct: Int, grams: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.XS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TrackyBodyText(text = label)
        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
            TrackyBodyText(text = "$pct%")
            TrackyBodySmall(
                text = "${grams.toInt()}g",
                color = TrackyColors.TextTertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MacroDistributionSheet(
    carbsPct: Int,
    proteinPct: Int,
    fatPct: Int,
    onCarbsChanged: (Int) -> Unit,
    onProteinChanged: (Int) -> Unit,
    onFatChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val total = carbsPct + proteinPct + fatPct
    val isValid = total == 100

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Macro Distribution"
    ) {
        Column {
            MacroSlider(
                label = "Carbs",
                value = carbsPct,
                onValueChange = onCarbsChanged
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            MacroSlider(
                label = "Protein",
                value = proteinPct,
                onValueChange = onProteinChanged
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            MacroSlider(
                label = "Fat",
                value = fatPct,
                onValueChange = onFatChanged
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyInfoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(text = "Total")
                    TrackyBodyText(
                        text = "$total%",
                        color = if (isValid) TrackyColors.Success else TrackyColors.Error
                    )
                }
            }

            TrackySheetActions(
                primaryText = "Done",
                onPrimaryClick = onDismiss,
                primaryEnabled = isValid
            )
        }
    }
}

@Composable
private fun MacroSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = label)
            TrackyBodyText(
                text = "$value%",
                color = TrackyColors.BrandPrimary
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            steps = 99,
            colors = SliderDefaults.colors(
                thumbColor = TrackyColors.BrandPrimary,
                activeTrackColor = TrackyColors.BrandPrimary,
                inactiveTrackColor = TrackyColors.Neutral100
            )
        )
    }
}
