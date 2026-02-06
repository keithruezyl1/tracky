package com.tracky.app.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.SavedEntry
import com.tracky.app.domain.model.SavedEntryType
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyEntryCard
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedEntriesScreen(
    viewModel: SavedEntriesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TrackyTopBarWithBack(
                title = "Saved Entries",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(TrackyColors.Background)
                .padding(paddingValues)
                .padding(horizontal = TrackyTokens.Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            item {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
            }

            if (uiState.entries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(TrackyTokens.Spacing.XL),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TrackyBodyText(
                            text = "No saved entries yet",
                            color = TrackyColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                        TrackyBodySmall(
                            text = "Save frequently used meals and exercises for quick logging",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            } else {
                // Food entries
                val foodEntries = uiState.entries.filter { it.entryType == SavedEntryType.FOOD }
                if (foodEntries.isNotEmpty()) {
                    item {
                        TrackySectionTitle(text = "Food")
                    }
                    items(foodEntries) { entry ->
                        SavedEntryRow(
                            entry = entry,
                            onUse = { viewModel.useSavedEntry(entry) },
                            onDelete = { viewModel.deleteSavedEntry(entry.id) }
                        )
                    }
                }

                // Exercise entries
                val exerciseEntries = uiState.entries.filter { it.entryType == SavedEntryType.EXERCISE }
                if (exerciseEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                        TrackySectionTitle(text = "Exercise")
                    }
                    items(exerciseEntries) { entry ->
                        SavedEntryRow(
                            entry = entry,
                            onUse = { viewModel.useSavedEntry(entry) },
                            onDelete = { viewModel.deleteSavedEntry(entry.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
            }
        }
    }
}

@Composable
private fun SavedEntryRow(
    entry: SavedEntry,
    onUse: () -> Unit,
    onDelete: () -> Unit
) {
    TrackyEntryCard(onClick = onUse) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (entry.entryType == SavedEntryType.FOOD)
                        Icons.Outlined.Restaurant
                    else
                        Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    tint = if (entry.entryType == SavedEntryType.FOOD)
                        TrackyColors.BrandPrimary
                    else
                        TrackyColors.Success
                )
                Column(
                    modifier = Modifier.padding(start = TrackyTokens.Spacing.S)
                ) {
                    TrackyBodyText(text = entry.name, maxLines = 1)
                    TrackyBodySmall(
                        text = "${entry.totalCalories} kcal",
                        color = TrackyColors.TextTertiary
                    )
                }
            }
            Row {
                TrackyBodySmall(
                    text = "Used ${entry.useCount}x",
                    color = TrackyColors.TextTertiary
                )
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
}
