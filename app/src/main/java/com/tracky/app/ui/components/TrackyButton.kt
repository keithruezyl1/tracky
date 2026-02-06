package com.tracky.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Tracky Primary Button
 * 
 * Height: 48dp
 * Radius: 14dp
 * Fill: Brand/Primary
 * Text: Neutral/0
 * Pressed: Brand/Deep
 */
@Composable
fun TrackyButtonPrimary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> TrackyColors.Neutral200
            isPressed -> TrackyColors.BrandDeep
            else -> TrackyColors.BrandPrimary
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "buttonColor"
    )

    Button(
        onClick = {
            if (withHaptics) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier.height(TrackyTokens.Sizes.ButtonHeight),
        enabled = enabled,
        shape = TrackyShapes.Medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = TrackyColors.TextOnPrimary,
            disabledContainerColor = TrackyColors.Neutral200,
            disabledContentColor = TrackyColors.Neutral500
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(
            horizontal = TrackyTokens.Spacing.M,
            vertical = TrackyTokens.Spacing.S
        ),
        content = content
    )
}

/**
 * Convenience overload with text label
 */
@Composable
fun TrackyButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true
) {
    TrackyButtonPrimary(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        withHaptics = withHaptics
    ) {
        Text(
            text = text,
            style = TrackyTypography.Button
        )
    }
}

/**
 * Tracky Secondary Button
 * 
 * Height: 48dp
 * Radius: 14dp
 * Fill: transparent
 * Border: 1dp Brand/Primary @ 60-80%
 * Text: Brand/Primary
 */
@Composable
fun TrackyButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> TrackyColors.Neutral200
            isPressed -> TrackyColors.BrandDeep
            else -> TrackyColors.BrandPrimary.copy(alpha = 0.7f)
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "borderColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> TrackyColors.Neutral500
            isPressed -> TrackyColors.BrandDeep
            else -> TrackyColors.BrandPrimary
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "contentColor"
    )

    OutlinedButton(
        onClick = {
            if (withHaptics) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier.height(TrackyTokens.Sizes.ButtonHeight),
        enabled = enabled,
        shape = TrackyShapes.Medium,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
            disabledContentColor = TrackyColors.Neutral500
        ),
        border = BorderStroke(
            width = TrackyTokens.Sizes.BorderWidth,
            color = borderColor
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(
            horizontal = TrackyTokens.Spacing.M,
            vertical = TrackyTokens.Spacing.S
        ),
        content = content
    )
}

/**
 * Convenience overload with text label
 */
@Composable
fun TrackyButtonSecondary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true
) {
    TrackyButtonSecondary(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        withHaptics = withHaptics
    ) {
        Text(
            text = text,
            style = TrackyTypography.Button
        )
    }
}

/**
 * Tracky Tertiary Button
 * 
 * Text-only, Brand/Primary
 */
@Composable
fun TrackyButtonTertiary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current

    TextButton(
        onClick = {
            if (withHaptics) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        shape = TrackyShapes.Medium,
        colors = ButtonDefaults.textButtonColors(
            contentColor = TrackyColors.BrandPrimary,
            disabledContentColor = TrackyColors.Neutral500
        ),
        contentPadding = PaddingValues(
            horizontal = TrackyTokens.Spacing.S,
            vertical = TrackyTokens.Spacing.XS
        ),
        content = content
    )
}

/**
 * Convenience overload with text label
 */
@Composable
fun TrackyButtonTertiary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true
) {
    TrackyButtonTertiary(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        withHaptics = withHaptics
    ) {
        Text(
            text = text,
            style = TrackyTypography.Button
        )
    }
}

/**
 * Tracky Danger Button
 * 
 * For destructive actions like delete/reset.
 * Fill: Error color
 * Text: White
 */
@Composable
fun TrackyButtonDanger(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    withHaptics: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) TrackyColors.Error.copy(alpha = 0.8f) else TrackyColors.Error,
        animationSpec = tween(100),
        label = "dangerBg"
    )

    Button(
        onClick = {
            if (withHaptics) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier.height(TrackyTokens.Sizes.ButtonHeight),
        enabled = enabled,
        shape = TrackyShapes.Medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = TrackyColors.Neutral0,
            disabledContainerColor = TrackyColors.Neutral200,
            disabledContentColor = TrackyColors.Neutral500
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            style = TrackyTypography.Button
        )
    }
}
