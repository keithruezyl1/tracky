package com.tracky.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography
import kotlinx.coroutines.delay

/**
 * Tracky Bottom Sheet
 * 
 * Uses Material 3 ModalBottomSheet with:
 * - Radius (top): 24dp
 * - Drag handle: subtle capsule
 * - Sheet sections separated by dividers (Neutral/200)
 * - Primary action anchored at bottom when needed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackyBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = TrackyShapes.BottomSheet,
        containerColor = TrackyColors.Surface,
        contentColor = TrackyColors.TextPrimary,
        tonalElevation = 0.dp,
        scrimColor = TrackyTokens.Colors.Neutral900.copy(alpha = 0.32f),
        dragHandle = null, // Remove the grey divider
        windowInsets = WindowInsets(0)
    ) {
        var contentVisible by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            delay(50) // Small delay for smoother animation
            contentVisible = true
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = TrackyTokens.Spacing.ScreenPadding)
                .padding(top = TrackyTokens.Spacing.M)
                .padding(bottom = TrackyTokens.Spacing.L)
        ) {
            // Title
            if (title != null) {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        tween(TrackyTokens.Animation.DurationMicro, easing = FastOutSlowInEasing)
                    ) + slideInVertically(
                        tween(TrackyTokens.Animation.DurationMicro, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    ) + scaleIn(
                        tween(TrackyTokens.Animation.DurationMicro, easing = FastOutSlowInEasing),
                        initialScale = 0.95f
                    )
                ) {
                    Column {
                        Text(
                            text = title,
                            style = TrackyTypography.HeadlineMedium,
                            color = TrackyColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                    }
                }
            }

            // Content with animation
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(
                    tween(TrackyTokens.Animation.DurationMicro, delayMillis = 50, easing = FastOutSlowInEasing)
                ) + slideInVertically(
                    tween(TrackyTokens.Animation.DurationMicro, delayMillis = 50, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 4 }
                ) + scaleIn(
                    tween(TrackyTokens.Animation.DurationMicro, delayMillis = 50, easing = FastOutSlowInEasing),
                    initialScale = 0.95f
                )
            ) {
                Column {
                    content()
                }
            }
        }
    }
}

/**
 * Tracky Drag Handle
 * 
 * Subtle capsule drag handle for bottom sheets.
 */
@Composable
fun TrackyDragHandle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.S)
            .height(4.dp)
            .clip(TrackyShapes.Pill)
            .background(TrackyColors.Neutral200),
        contentAlignment = Alignment.Center
    ) {}
}

/**
 * Tracky Sheet Divider
 * 
 * Divider for separating bottom sheet sections.
 */
@Composable
fun TrackySheetDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.M),
        thickness = TrackyTokens.Sizes.BorderWidth,
        color = TrackyColors.Divider
    )
}

/**
 * Tracky Sheet Action Button Row
 * 
 * Bottom-anchored action buttons for sheets.
 */
@Composable
fun TrackySheetActions(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryEnabled: Boolean = true,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TrackySheetDivider()

        if (secondaryText != null && onSecondaryClick != null) {
            TrackyButtonSecondary(
                text = secondaryText,
                onClick = onSecondaryClick,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        }

        TrackyButtonPrimary(
            text = primaryText,
            onClick = onPrimaryClick,
            enabled = primaryEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
