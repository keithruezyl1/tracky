package com.tracky.app.ui.screens.entrydetail

import com.tracky.app.ui.components.SwipeableRow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyCardTitle
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyFullScreenLoading
import com.tracky.app.ui.components.TrackyInfoCard
import com.tracky.app.ui.components.TrackyMacrosRow
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    entryId: Long,
    entryType: String,
    viewModel: EntryDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEntryDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var showActionsSheet by remember { mutableStateOf(false) }
    var showSaveTemplateSheet by remember { mutableStateOf(false) }
    var showDateTimeSheet by remember { mutableStateOf(false) }

    // Navigate back when entry is deleted (e.g., when last food item is removed)
    LaunchedEffect(uiState.entryDeleted) {
        if (uiState.entryDeleted) {
            onEntryDeleted()
        }
    }

    Scaffold(
        topBar = {
            val title = if (entryType == "food") {
                val items = uiState.foodEntry?.items
                if (!items.isNullOrEmpty()) {
                    val firstItem = items.first().name
                    val remainingCount = items.size - 1
                    
                    if (remainingCount > 0) {
                        buildAnnotatedString {
                            append(firstItem)
                            withStyle(SpanStyle(color = TrackyColors.TextTertiary)) {
                                append(" + $remainingCount others")
                            }
                        }
                    } else {
                        AnnotatedString(firstItem)
                    }
                } else {
                    AnnotatedString("Food Entry")
                }
            } else {
                AnnotatedString("Exercise Entry")
            }

            TrackyTopBarWithBack(
                title = title,
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showActionsSheet = true }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Actions",
                            tint = TrackyColors.TextSecondary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            TrackyFullScreenLoading()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TrackyColors.Background)
                    .padding(paddingValues)
                    .padding(horizontal = TrackyTokens.Spacing.ScreenPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)
            ) {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

                when {
                    uiState.foodEntry != null -> FoodEntryDetail(
                        entry = uiState.foodEntry!!,
                        onItemDelete = viewModel::deleteFoodItem
                    )
                    uiState.exerciseEntry != null -> ExerciseEntryDetail(uiState.exerciseEntry!!)
                }

                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
            }
        }
    }

    // Actions sheet
    if (showActionsSheet) {
        EntryActionsSheet(
            onDismiss = { showActionsSheet = false },
            onEdit = { showEditSheet = true },
            onAdjust = { showEditSheet = true }, // Same as edit for now
            onChangeDateTime = { showDateTimeSheet = true },
            onSaveTemplate = { showSaveTemplateSheet = true },
            onDelete = {
                viewModel.deleteEntry()
                onEntryDeleted()
            }
        )
    }

    // Edit sheets
    if (showEditSheet) {
        uiState.foodEntry?.let { entry ->
            EditFoodEntrySheet(
                entry = entry,
                onDismiss = { showEditSheet = false },
                onSave = { updatedEntry ->
                    viewModel.updateFoodEntry(updatedEntry)
                    showEditSheet = false
                }
            )
        }
        uiState.exerciseEntry?.let { entry ->
            EditExerciseEntrySheet(
                entry = entry,
                onDismiss = { showEditSheet = false },
                onSave = { updatedEntry ->
                    viewModel.updateExerciseEntry(updatedEntry)
                    showEditSheet = false
                }
            )
        }
    }

    // Save template sheet
    if (showSaveTemplateSheet) {
        val suggestedName = uiState.foodEntry?.items?.firstOrNull()?.name
            ?: uiState.exerciseEntry?.activityName
            ?: "My Template"

        SaveTemplateSheet(
            suggestedName = suggestedName,
            onDismiss = { showSaveTemplateSheet = false },
            onSave = { name ->
                viewModel.saveAsTemplate(name)
                showSaveTemplateSheet = false
            }
        )
    }

    // Change date/time sheet
    if (showDateTimeSheet) {
        val currentDate = uiState.foodEntry?.date ?: uiState.exerciseEntry?.date ?: ""
        val currentTime = uiState.foodEntry?.time ?: uiState.exerciseEntry?.time ?: ""

        ChangeDateTimeSheet(
            currentDate = currentDate,
            currentTime = currentTime,
            onDismiss = { showDateTimeSheet = false },
            onSave = { date, time ->
                viewModel.updateDateTime(date, time)
                showDateTimeSheet = false
            }
        )
    }
}

@Composable
private fun FoodEntryDetail(
    entry: FoodEntry,
    onItemDelete: (FoodItem) -> Unit
) {
    // Summary card
    TrackyCard {
        TrackyCardTitle(text = "Summary")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = "Total Calories")
            TrackyBodyText(
                text = "${entry.totalCalories} kcal",
                color = TrackyColors.BrandPrimary
            )
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        TrackyMacrosRow(
            carbsConsumed = entry.totalCarbsG,
            carbsTarget = entry.totalCarbsG,
            proteinConsumed = entry.totalProteinG,
            proteinTarget = entry.totalProteinG,
            fatConsumed = entry.totalFatG,
            fatTarget = entry.totalFatG
        )
    }

    // Analysis narrative - hidden from UI for now
    // entry.analysisNarrative?.let { narrative ->
    //     TrackyInfoCard {
    //         TrackySectionTitle(text = "Analysis")
    //         Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
    //         TrackyBodySmall(
    //             text = narrative,
    //             color = TrackyTokens.Colors.TextSecondary
    //         )
    //     }
    // }

    // Items breakdown
    TrackySectionTitle(text = "Items")

    entry.items.forEach { item ->
        SwipeableRow(onDelete = { onItemDelete(item) }) {
            FoodItemRow(item = item)
        }
    }

    // Metadata
    TrackyCard {
        TrackyCardTitle(text = "Details")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        DetailRow("Date", entry.date)
        DetailRow("Time", entry.time.take(5))

        entry.originalInput?.let { input ->
            DetailRow("Original Input", input)
        }
    }
}

@Composable
private fun FoodItemRow(item: FoodItem) {
    TrackyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TrackyBodyText(text = item.name)
                item.matchedName?.let { matched ->
                    TrackyBodySmall(
                        text = "Matched: $matched",
                        color = TrackyColors.TextTertiary
                    )
                }
                TrackyBodySmall(
                    text = "${item.quantity} ${item.unit}",
                    color = TrackyColors.TextSecondary
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                TrackyBodyText(text = "${item.calories} kcal")
                TrackyBodySmall(
                    text = "C: ${item.carbsG.toInt()}g P: ${item.proteinG.toInt()}g F: ${item.fatG.toInt()}g",
                    color = TrackyColors.TextTertiary
                )
            }
        }

        // Provenance
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
        TrackyBodySmall(
            text = "Source: ${item.provenance.source.value} (${(item.provenance.confidence * 100).toInt()}% confidence)",
            color = TrackyColors.TextTertiary
        )
    }
}

@Composable
private fun ExerciseEntryDetail(entry: ExerciseEntry) {
    // Summary card
    TrackyCard {
        TrackyCardTitle(text = entry.activityName)
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = "Duration")
            TrackyBodyText(text = "${entry.durationMinutes} min")
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyDivider()
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = "Calories Burned")
            TrackyBodyText(
                text = "${entry.caloriesBurned} kcal",
                color = TrackyColors.Success
            )
        }

        entry.intensity?.let { intensity ->
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
            TrackyDivider()
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackyBodyText(text = "Intensity")
                TrackyBodyText(text = intensity.value.replaceFirstChar { it.uppercase() })
            }
        }
    }

    // Formula breakdown
    TrackyInfoCard {
        TrackySectionTitle(text = "Calculation")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        val formula = "MET ${entry.metValue} x ${entry.userWeightKg}kg x ${entry.durationMinutes / 60f}h"
        TrackyBodySmall(
            text = formula,
            color = TrackyColors.TextSecondary
        )
        TrackyBodySmall(
            text = "= ${entry.caloriesBurned} kcal",
            color = TrackyColors.BrandPrimary
        )
    }

    // Metadata
    TrackyCard {
        TrackyCardTitle(text = "Details")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        DetailRow("Date", entry.date)
        DetailRow("Time", entry.time.take(5))
        DetailRow("Source", entry.provenance.source.value)

        entry.originalInput?.let { input ->
            DetailRow("Original Input", input)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.XXS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TrackyBodySmall(
            text = label,
            color = TrackyColors.TextTertiary
        )
        TrackyBodySmall(text = value)
    }
}
