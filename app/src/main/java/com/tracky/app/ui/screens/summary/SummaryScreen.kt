package com.tracky.app.ui.screens.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyCardTitle
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import com.tracky.app.ui.theme.TrackyTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val timeframe by viewModel.timeframe.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TrackyTopBarWithBack(
                title = "Summary",
                onBackClick = onNavigateBack
            )
        },
        containerColor = TrackyColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Timeframe Tabs
            LazyRow(
                contentPadding = PaddingValues(horizontal = TrackyTokens.Spacing.ScreenPadding),
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S),
                modifier = Modifier.padding(vertical = TrackyTokens.Spacing.M)
            ) {
                items(SummaryTimeframe.entries.toTypedArray()) { item ->
                    TrackyChip(
                        label = item.label,
                        selected = timeframe == item,
                        onClick = { viewModel.setTimeframe(item) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = TrackyTokens.Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M),
                modifier = Modifier.weight(1f)
            ) {
                // Averages Card
                item {
                    TrackyCard {
                        TrackyCardTitle(text = "Averages")
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                TrackyBodySmall(text = "Avg Calories", color = TrackyColors.TextSecondary)
                                Text(
                                    text = "${uiState.averageCalories} kcal",
                                    style = TrackyTypography.HeadlineMedium,
                                    color = TrackyColors.TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                TrackyBodySmall(text = "Total Intake", color = TrackyColors.TextSecondary)
                                Text(
                                    text = "${uiState.totalCalories} kcal",
                                    style = TrackyTypography.HeadlineMedium,
                                    color = TrackyColors.TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                        TrackyDivider()
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MacroStat("Protein", "${uiState.averageProtein.toInt()}g")
                            MacroStat("Carbs", "${uiState.averageCarbs.toInt()}g")
                            MacroStat("Fat", "${uiState.averageFat.toInt()}g")
                        }
                    }
                }

                // Weight Card
                item {
                    TrackyCard {
                        TrackyCardTitle(text = "Weight Change")
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                TrackyBodySmall(text = "Start", color = TrackyColors.TextSecondary)
                                Text(
                                    text = uiState.startWeight?.let { String.format("%.1f", it) } ?: "-",
                                    style = TrackyTypography.HeadlineMedium
                                )
                            }
                            
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = TrackyColors.TextTertiary
                            )

                            Column {
                                TrackyBodySmall(text = "End", color = TrackyColors.TextSecondary)
                                Text(
                                    text = uiState.endWeight?.let { String.format("%.1f", it) } ?: "-",
                                    style = TrackyTypography.HeadlineMedium
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                TrackyBodySmall(text = "Change", color = TrackyColors.TextSecondary)
                                val change = uiState.weightChange
                                val color = if (change < 0) TrackyColors.BrandPrimary else if (change > 0) TrackyColors.Error else TrackyColors.TextPrimary
                                val sign = if (change > 0) "+" else ""
                                Text(
                                    text = "$sign${String.format("%.1f", change)}",
                                    style = TrackyTypography.HeadlineMedium,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroStat(label: String, value: String) {
    Column {
        TrackyBodySmall(text = label, color = TrackyColors.TextSecondary)
        Text(
            text = value,
            style = TrackyTypography.HeadlineMedium,
            color = TrackyColors.TextPrimary
        )
    }
}
