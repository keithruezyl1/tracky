package com.tracky.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Tracky Progress Bar
 * 
 * Track: Neutral/100
 * Fill: Brand/Primary
 * Warning state: Warning
 * Always paired with numeric label (no color-only encoding)
 */
@Composable
fun TrackyProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    showWarning: Boolean = false,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = if (animated) TrackyTokens.Animation.DurationNavigation else 0
        ),
        label = "progress"
    )

    val fillColor = if (showWarning) {
        TrackyColors.Warning
    } else {
        TrackyColors.BrandPrimary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(TrackyShapes.Pill)
            .background(TrackyColors.Neutral100)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(TrackyShapes.Pill)
                .background(fillColor)
        )
    }
}

/**
 * Tracky Calories Progress
 * 
 * Displays remaining calories with progress bar.
 */
@Composable
fun TrackyCaloriesProgress(
    consumed: Int,
    burned: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val remaining = goal - consumed + burned
    val progress = (consumed.toFloat() / goal).coerceIn(0f, 1f)
    val isOverGoal = remaining < 0

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "$consumed",
                    style = TrackyTypography.HeadlineLarge,
                    color = TrackyColors.TextPrimary
                )
                Text(
                    text = "consumed",
                    style = TrackyTypography.LabelSmall,
                    color = TrackyColors.TextTertiary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (remaining >= 0) remaining else 0}",
                    style = TrackyTypography.HeadlineMedium,
                    color = if (isOverGoal) TrackyColors.Warning else TrackyColors.BrandPrimary
                )
                Text(
                    text = "remaining",
                    style = TrackyTypography.LabelSmall,
                    color = TrackyColors.TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        TrackyProgress(
            progress = progress,
            showWarning = isOverGoal
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Goal: $goal kcal",
                style = TrackyTypography.LabelSmall,
                color = TrackyColors.TextTertiary
            )
            if (burned > 0) {
                Text(
                    text = "+$burned burned",
                    style = TrackyTypography.LabelSmall,
                    color = TrackyColors.Success
                )
            }
        }
    }
}

/**
 * Tracky Macro Progress
 * 
 * Displays a single macro with progress bar.
 */
@Composable
fun TrackyMacroProgress(
    label: String,
    consumed: Float,
    target: Float,
    unit: String = "g",
    color: Color = TrackyColors.BrandPrimary,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (consumed / target).coerceIn(0f, 1f) else 0f
    val isOverTarget = consumed > target

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TrackyTypography.BodyMedium,
                color = TrackyColors.TextSecondary
            )
            Text(
                text = "${consumed.toInt()}/${target.toInt()}$unit",
                style = TrackyTypography.BodyMedium,
                color = if (isOverTarget) TrackyColors.Warning else TrackyColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XXS))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(TrackyShapes.Pill)
                .background(TrackyColors.Neutral100)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(TrackyShapes.Pill)
                    .background(color)
            )
        }
    }
}

/**
 * Tracky Macro Pill
 * 
 * Compact pill showing macro name and value with thin progress bar underneath.
 */
@Composable
fun TrackyMacroPill(
    label: String,
    consumed: Float,
    target: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (consumed / target).coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = TrackyTypography.LabelSmall,
                color = TrackyColors.TextSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${consumed.toInt()}/${target.toInt()}g",
                style = TrackyTypography.LabelSmall,
                color = TrackyColors.TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(TrackyShapes.Pill)
                .background(TrackyColors.Neutral100)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(TrackyShapes.Pill)
                    .background(color)
            )
        }
    }
}

/**
 * Tracky Macros Row
 * 
 * Displays all three macros as compact pills in a single row.
 */
@Composable
fun TrackyMacrosRow(
    carbsConsumed: Float,
    carbsTarget: Float,
    proteinConsumed: Float,
    proteinTarget: Float,
    fatConsumed: Float,
    fatTarget: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
    ) {
        TrackyMacroPill(
            label = "Carbs",
            consumed = carbsConsumed,
            target = carbsTarget,
            color = TrackyColors.Success,
            modifier = Modifier.weight(1f)
        )
        
        TrackyMacroPill(
            label = "Protein",
            consumed = proteinConsumed,
            target = proteinTarget,
            color = TrackyColors.Warning,
            modifier = Modifier.weight(1f)
        )
        
        TrackyMacroPill(
            label = "Fat",
            consumed = fatConsumed,
            target = fatTarget,
            color = Color(0xFFFFD60A), // Yellow
            modifier = Modifier.weight(1f)
        )
    }
}
