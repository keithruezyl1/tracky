package com.tracky.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Tracky Chip
 * 
 * Used for 7-day strip and filters.
 * 
 * Height: 28-32dp
 * Radius: 10 or pill
 * Default: Neutral/0 with Neutral/200 border
 * Selected: fill Brand/Tint, text Brand/Primary
 */
@Composable
fun TrackyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    usePillShape: Boolean = false,
    compact: Boolean = false,
    tint: Color? = null
) {
    val haptic = LocalHapticFeedback.current

    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> TrackyColors.Neutral100
            selected -> tint?.copy(alpha = 0.1f) ?: TrackyColors.BrandTint
            else -> TrackyColors.Surface
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "containerColor"
    )

    val labelColor by animateColorAsState(
        targetValue = when {
            !enabled -> TrackyColors.TextTertiary
            selected -> tint ?: TrackyColors.BrandPrimary
            else -> TrackyColors.TextPrimary
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "labelColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> TrackyColors.Neutral100
            selected -> (tint ?: TrackyColors.BrandPrimary).copy(alpha = 0.3f)
            else -> TrackyColors.Border
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "borderColor"
    )

    FilterChip(
        selected = selected,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        label = {
            Text(
                text = label,
                style = if (compact) TrackyTypography.LabelSmall else TrackyTypography.BodyMedium,
                color = labelColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                // Removed fillMaxWidth modifier to prevent forced expansion
            )
        },
        modifier = modifier.height(
            if (compact) TrackyTokens.Sizes.ChipHeightSmall else TrackyTokens.Sizes.ChipHeight
        ),
        enabled = enabled,
        shape = if (usePillShape) TrackyShapes.Pill else TrackyShapes.ExtraSmall,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            selectedContainerColor = containerColor,
            disabledContainerColor = TrackyColors.Neutral100,
            labelColor = labelColor,
            selectedLabelColor = labelColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = borderColor,
            selectedBorderColor = borderColor,
            borderWidth = TrackyTokens.Sizes.BorderWidth,
            selectedBorderWidth = TrackyTokens.Sizes.BorderWidth
        )
    )
}


/**
 * Day status for calendar strip
 */
enum class DayStatus {
    NONE,       // No entries, future/today
    HAS_ENTRY,  // Has entries (today/future), show yellow
    SUCCESS,    // Past day under goal (show green)
    FAILURE     // Past day over goal (show red)
}

/**
 * Tracky Day Chip
 * 
 * Specialized chip for 7-day strip display.
 * Shows day letter and date number.
 * Supports status coloring: 
 * - Yellow for today/future with entries
 * - Green for past days under goal
 * - Red for past days over goal
 */
@Composable
fun TrackyDayChip(
    dayLetter: String,
    dateNumber: String,
    selected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    status: DayStatus = DayStatus.NONE
) {
    val haptic = LocalHapticFeedback.current

    val containerColor by animateColorAsState(
        targetValue = when {
            selected -> TrackyColors.BrandTint
            status == DayStatus.FAILURE -> TrackyColors.Error.copy(alpha = 0.1f)
            status == DayStatus.SUCCESS -> TrackyColors.Success.copy(alpha = 0.1f)
            status == DayStatus.HAS_ENTRY -> TrackyColors.Warning.copy(alpha = 0.1f)
            else -> Color.Transparent
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "containerColor"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            selected -> TrackyColors.BrandPrimary
            isToday -> TrackyColors.BrandPrimary
            status == DayStatus.FAILURE -> TrackyColors.Error
            status == DayStatus.SUCCESS -> TrackyColors.Success
            status == DayStatus.HAS_ENTRY -> TrackyColors.Warning
            else -> TrackyColors.TextSecondary
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "textColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            selected -> TrackyColors.BrandPrimary.copy(alpha = 0.3f)
            status == DayStatus.FAILURE -> TrackyColors.Error.copy(alpha = 0.3f)
            status == DayStatus.SUCCESS -> TrackyColors.Success.copy(alpha = 0.3f)
            status == DayStatus.HAS_ENTRY -> TrackyColors.Warning.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "borderColor"
    )

    FilterChip(
        selected = selected,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        label = {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = TrackyTokens.Spacing.XXS)
            ) {
                Text(
                    text = dayLetter,
                    style = TrackyTypography.LabelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
                Text(
                    text = dateNumber,
                    style = TrackyTypography.BodyMedium.copy(
                        fontWeight = if (selected || isToday) 
                            androidx.compose.ui.text.font.FontWeight.SemiBold 
                        else 
                            androidx.compose.ui.text.font.FontWeight.Normal
                    ),
                    color = textColor
                )
            }
        },
        modifier = modifier,
        shape = TrackyShapes.ExtraSmall,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            selectedContainerColor = containerColor,
            labelColor = textColor,
            selectedLabelColor = textColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = borderColor,
            selectedBorderColor = borderColor,
            borderWidth = TrackyTokens.Sizes.BorderWidth,
            selectedBorderWidth = TrackyTokens.Sizes.BorderWidth
        )
    )
}
