package com.tracky.app.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.TrackyButtonPrimary
import com.tracky.app.ui.components.TrackyButtonSecondary
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyCardTitle
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyInfoCard
import com.tracky.app.ui.components.TrackyNumberInput
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalsScreen(
    viewModel: DailyGoalsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMacroSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TrackyTopBarWithBack(
                title = "Daily Goals",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TrackyColors.Background)
                .padding(paddingValues)
                .padding(horizontal = TrackyTokens.Spacing.ScreenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            // Calorie goal section
            TrackySectionTitle(text = "Calorie Goal")
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            TrackyNumberInput(
                value = uiState.calorieGoal.toString(),
                onValueChange = { viewModel.setCalorieGoal(it.toIntOrNull() ?: 2000) },
                label = "Daily Calorie Target",
                suffix = "kcal",
                allowDecimal = false
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            // Quick presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
            ) {
                listOf(1500, 1800, 2000, 2200, 2500).forEach { preset ->
                    TrackyChip(
                        label = "$preset",
                        selected = uiState.calorieGoal == preset,
                        onClick = { viewModel.setCalorieGoal(preset) },
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

            // Macro distribution section
            TrackySectionTitle(text = "Macro Distribution")
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            TrackyCard {
                MacroDistributionRow(
                    label = "Carbs",
                    pct = uiState.carbsPct,
                    grams = uiState.carbsG
                )
                TrackyDivider()
                MacroDistributionRow(
                    label = "Protein",
                    pct = uiState.proteinPct,
                    grams = uiState.proteinG
                )
                TrackyDivider()
                MacroDistributionRow(
                    label = "Fat",
                    pct = uiState.fatPct,
                    grams = uiState.fatG
                )
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyButtonSecondary(
                text = "Edit Distribution",
                onClick = { showMacroSheet = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

            // Save button
            TrackyButtonPrimary(
                text = "Save Changes",
                onClick = {
                    viewModel.saveGoals()
                    onNavigateBack()
                },
                enabled = uiState.isValid,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
        }
    }

    // Macro distribution sheet
    if (showMacroSheet) {
        MacroDistributionSheet(
            carbsPct = uiState.carbsPct,
            proteinPct = uiState.proteinPct,
            fatPct = uiState.fatPct,
            onCarbsChanged = viewModel::setCarbsPct,
            onProteinChanged = viewModel::setProteinPct,
            onFatChanged = viewModel::setFatPct,
            onDismiss = { showMacroSheet = false }
        )
    }
}

@Composable
private fun MacroDistributionRow(
    label: String,
    pct: Int,
    grams: Float
) {
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
