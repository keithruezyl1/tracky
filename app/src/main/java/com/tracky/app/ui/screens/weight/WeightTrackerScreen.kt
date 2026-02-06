package com.tracky.app.ui.screens.weight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.WeightChartRange
import com.tracky.app.domain.model.WeightEntry
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.TrackyButtonDanger
import com.tracky.app.ui.components.TrackyButtonSecondary
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyCardTitle
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyInput
import com.tracky.app.ui.components.TrackyNumberInput
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.components.WeightChart
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerScreen(
    viewModel: WeightTrackerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedRange by viewModel.selectedRange.collectAsState()

    Scaffold(
        topBar = {
            TrackyTopBarWithBack(
                title = "Weight",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddDialog,
                containerColor = TrackyColors.BrandPrimary,
                contentColor = TrackyColors.TextOnPrimary
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Weight")
            }
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

            // Current vs Target
            TrackyCard(onClick = viewModel::showWeightSelectionDialog) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        TrackyBodySmall(text = "Current Weight")
                        TrackyCardTitle(text = String.format(Locale.getDefault(), "%.1f kg", uiState.currentWeightKg))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        TrackyBodySmall(text = "Goal")
                        TrackyCardTitle(text = String.format(Locale.getDefault(), "%.1f kg", uiState.targetWeightKg))
                    }
                }

                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

                // Simple Progress Info
                val startWeight = uiState.startWeightKg ?: uiState.currentWeightKg
                val totalProgress = startWeight - uiState.currentWeightKg
                val targetDiff = uiState.currentWeightKg - uiState.targetWeightKg

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(
                        text = if (totalProgress >= 0) {
                            String.format(Locale.getDefault(), "Lost %.1f kg total", totalProgress)
                        } else {
                            String.format(Locale.getDefault(), "Gained %.1f kg total", -totalProgress)
                        },
                        color = if (totalProgress >= 0) TrackyColors.Success else TrackyColors.Error
                    )
                    TrackyBodyText(
                        text = String.format(Locale.getDefault(), "%.1f kg left", kotlin.math.abs(targetDiff)),
                        color = TrackyColors.BrandPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

            // Weight Chart Section
            TrackySectionTitle(text = "Progress")
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            // Range Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
            ) {
                WeightChartRange.entries.forEach { range ->
                    TrackyChip(
                        label = range.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = selectedRange == range,
                        onClick = { viewModel.selectRange(range) },
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            // Chart
            if (uiState.entries.size < 2) {
                TrackyCard(modifier = Modifier.height(200.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TrackyBodySmall(
                            text = "Not enough data for chart yet",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            } else {
                TrackyCard {
                    WeightChart(
                        entries = uiState.entries,
                        targetWeightKg = uiState.targetWeightKg
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

            // Recent Entries
            TrackySectionTitle(text = "Recent History")
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            if (uiState.entries.isEmpty()) {
                TrackyBodySmall(
                    text = "No entries yet. Tap + to add your weight.",
                    color = TrackyColors.TextTertiary,
                    modifier = Modifier.padding(vertical = TrackyTokens.Spacing.M)
                )
            } else {
                val recentEntries = uiState.entries.take(10)
                recentEntries.forEachIndexed { index, entry ->
                    WeightEntryRow(
                        entry = entry,
                        onEdit = { viewModel.showEditDialog(entry) },
                        onDelete = { viewModel.deleteWeightEntry(entry.id) }
                    )
                    if (index < recentEntries.lastIndex) {
                        TrackyDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XXL))
        }
    }

    // Weight selection dialog (Current vs Goal)
    if (uiState.showWeightSelectionDialog) {
        TrackyBottomSheet(
            onDismissRequest = viewModel::hideWeightSelectionDialog,
            title = "Edit Weight"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
            ) {
                TrackyButtonSecondary(
                    text = "Update Current Weight",
                    onClick = {
                        viewModel.hideWeightSelectionDialog()
                        viewModel.showEditCurrentWeightDialog()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                TrackyButtonSecondary(
                    text = "Update Goal Weight",
                    onClick = {
                        viewModel.hideWeightSelectionDialog()
                        viewModel.showEditTargetWeightDialog()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Entry options dialog (Edit/Delete)
    if (uiState.showEntryOptionsDialog && uiState.selectedEntryForOptions != null) {
        uiState.selectedEntryForOptions?.let { entry ->
            TrackyBottomSheet(
                onDismissRequest = viewModel::hideEntryOptions,
                title = "Weight Entry"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
                ) {
                    TrackyButtonSecondary(
                        text = "Edit",
                        onClick = {
                            viewModel.hideEntryOptions()
                            viewModel.showEditDialog(entry)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TrackyButtonDanger(
                        text = "Delete",
                        onClick = {
                            viewModel.hideEntryOptions()
                            viewModel.deleteWeightEntry(entry.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Add weight dialog
    if (uiState.showAddDialog) {
        AddWeightSheet(
            onDismiss = viewModel::hideAddDialog,
            onSave = { weight, note ->
                viewModel.addWeightEntry(weight, note)
                viewModel.hideAddDialog()
            }
        )
    }

    // Edit weight dialog
    uiState.editingEntry?.let { entry ->
        EditWeightSheet(
            entry = entry,
            onDismiss = viewModel::hideEditDialog,
            onSave = { updatedEntry ->
                viewModel.updateWeightEntry(updatedEntry)
                viewModel.hideEditDialog()
            },
            onDelete = {
                viewModel.deleteWeightEntry(entry.id)
                viewModel.hideEditDialog()
            }
        )
    }

    // Edit current weight dialog
    if (uiState.showEditCurrentWeight) {
        EditCurrentWeightSheet(
            currentWeight = uiState.currentWeightKg,
            onDismiss = viewModel::hideEditCurrentWeightDialog,
            onSave = { weight ->
                viewModel.updateCurrentWeight(weight)
                viewModel.hideEditCurrentWeightDialog()
            }
        )
    }

    // Edit target weight dialog
    if (uiState.showEditTargetWeight) {
        EditTargetWeightSheet(
            targetWeight = uiState.targetWeightKg,
            onDismiss = viewModel::hideEditTargetWeightDialog,
            onSave = { weight ->
                viewModel.updateTargetWeight(weight)
                viewModel.hideEditTargetWeightDialog()
            }
        )
    }
}

@Composable
private fun WeightEntryRow(
    entry: WeightEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.M),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            TrackyBodyText(text = String.format(Locale.getDefault(), "%.1f kg", entry.weightKg))
            TrackyBodySmall(text = entry.date, color = TrackyColors.TextTertiary)
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = TrackyColors.TextSecondary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = TrackyColors.Error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWeightSheet(
    onDismiss: () -> Unit,
    onSave: (Float, String?) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Add Weight"
    ) {
        Column {
            TrackyNumberInput(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight",
                placeholder = "Enter weight",
                suffix = "kg"
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyInput(
                value = note,
                onValueChange = { note = it },
                label = "Note (optional)",
                placeholder = "Add a note"
            )

            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    weight.toFloatOrNull()?.let { w ->
                        onSave(w, note.ifBlank { null })
                    }
                },
                primaryEnabled = weight.toFloatOrNull() != null && weight.toFloatOrNull()!! > 0
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditWeightSheet(
    entry: WeightEntry,
    onDismiss: () -> Unit,
    onSave: (WeightEntry) -> Unit,
    onDelete: () -> Unit
) {
    var weight by remember { mutableStateOf(entry.weightKg.toString()) }
    var note by remember { mutableStateOf(entry.note ?: "") }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Edit Weight"
    ) {
        Column {
            TrackyNumberInput(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight",
                placeholder = "Enter weight",
                suffix = "kg"
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyInput(
                value = note,
                onValueChange = { note = it },
                label = "Note (optional)",
                placeholder = "Add a note"
            )

            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    weight.toFloatOrNull()?.let { w ->
                        onSave(
                            entry.copy(
                                weightKg = w,
                                note = note.ifBlank { null },
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                primaryEnabled = weight.toFloatOrNull() != null && weight.toFloatOrNull()!! > 0,
                secondaryText = "Delete",
                onSecondaryClick = onDelete
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCurrentWeightSheet(
    currentWeight: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var weight by remember { mutableStateOf(currentWeight.toString()) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Update Current Weight"
    ) {
        Column {
            TrackyNumberInput(
                value = weight,
                onValueChange = { weight = it },
                label = "Current Weight",
                placeholder = "Enter your current weight",
                suffix = "kg"
            )

            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    weight.toFloatOrNull()?.let { w ->
                        onSave(w)
                    }
                },
                primaryEnabled = weight.toFloatOrNull() != null && weight.toFloatOrNull()!! > 0
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTargetWeightSheet(
    targetWeight: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var weight by remember { mutableStateOf(targetWeight.toString()) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Update Target Weight"
    ) {
        Column {
            TrackyNumberInput(
                value = weight,
                onValueChange = { weight = it },
                label = "Target Weight",
                placeholder = "Enter your target weight",
                suffix = "kg"
            )

            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    weight.toFloatOrNull()?.let { w ->
                        onSave(w)
                    }
                },
                primaryEnabled = weight.toFloatOrNull() != null && weight.toFloatOrNull()!! > 0
            )
        }
    }
}
