package com.tracky.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.UnitPreference
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyButtonDanger
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackySwitch
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onResetComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val resetComplete by viewModel.resetComplete.collectAsState()

    // Navigate to onboarding when reset is complete
    LaunchedEffect(resetComplete) {
        if (resetComplete) {
            onResetComplete()
        }
    }

    // Reset confirmation dialog
    if (uiState.showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isResetting) viewModel.hideResetConfirmation() },
            title = { Text("Reset All Data?") },
            text = {
                Column {
                    Text("This will permanently delete all your data including:")
                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                    Text("• Profile information")
                    Text("• Food and exercise logs")
                    Text("• Weight history")
                    Text("• All settings")
                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                    Text("This action cannot be undone.")
                }
            },
            confirmButton = {
                if (uiState.isResetting) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(TrackyTokens.Spacing.S),
                        color = TrackyColors.Error
                    )
                } else {
                    TextButton(
                        onClick = viewModel::resetAllData
                    ) {
                        Text("Reset", color = TrackyColors.Error)
                    }
                }
            },
            dismissButton = {
                if (!uiState.isResetting) {
                    TextButton(onClick = viewModel::hideResetConfirmation) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TrackyTopBarWithBack(
                title = "Settings",
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)
        ) {
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            // Appearance section
            TrackySectionTitle(text = "Appearance")

            TrackyCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = TrackyTokens.Spacing.XS),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TrackyBodyText(text = "Dark Mode")
                        TrackyBodySmall(
                            text = "Use dark theme throughout the app",
                            color = TrackyColors.TextTertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(TrackyTokens.Spacing.M))
                    TrackySwitch(
                        checked = uiState.darkModeEnabled,
                        onCheckedChange = viewModel::setDarkModeEnabled
                    )
                }
            }

            // About section
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            TrackySectionTitle(text = "About")

            TrackyCard {
                SettingsRow("Version", "1.0.0")
                TrackyDivider()
                SettingsRow("Build", "1")
            }

            // Reset section
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyCard {
                Column {
                    TrackyBodyText(text = "Reset All Data")
                    TrackyBodySmall(
                        text = "Delete all data and start fresh with onboarding",
                        color = TrackyColors.TextTertiary
                    )
                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                    TrackyButtonDanger(
                        text = "Reset App",
                        onClick = viewModel::showResetConfirmation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.XS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TrackyBodyText(text = label)
        TrackyBodySmall(
            text = value,
            color = TrackyColors.TextTertiary
        )
    }
}
