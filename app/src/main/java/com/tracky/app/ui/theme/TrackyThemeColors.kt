package com.tracky.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Dynamic color accessor for Tracky theme.
 * Use this instead of TrackyTokens.Colors to ensure dark mode works correctly.
 * 
 * Example: TrackyThemeColors.TextPrimary instead of TrackyTokens.Colors.TextPrimary
 */
object TrackyThemeColors {
    private val colors: Any
        @Composable
        @ReadOnlyComposable
        get() = LocalTrackyColors.current

    val Neutral0: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral0
            is TrackyTokens.ColorsDark -> c.Neutral0
            else -> TrackyTokens.Colors.Neutral0
        }

    val Neutral50: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral50
            is TrackyTokens.ColorsDark -> c.Neutral50
            else -> TrackyTokens.Colors.Neutral50
        }

    val Neutral100: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral100
            is TrackyTokens.ColorsDark -> c.Neutral100
            else -> TrackyTokens.Colors.Neutral100
        }

    val Neutral200: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral200
            is TrackyTokens.ColorsDark -> c.Neutral200
            else -> TrackyTokens.Colors.Neutral200
        }

    val Neutral500: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral500
            is TrackyTokens.ColorsDark -> c.Neutral500
            else -> TrackyTokens.Colors.Neutral500
        }

    val Neutral700: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral700
            is TrackyTokens.ColorsDark -> c.Neutral700
            else -> TrackyTokens.Colors.Neutral700
        }

    val Neutral900: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Neutral900
            is TrackyTokens.ColorsDark -> c.Neutral900
            else -> TrackyTokens.Colors.Neutral900
        }

    val BrandPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.BrandPrimary
            is TrackyTokens.ColorsDark -> c.BrandPrimary
            else -> TrackyTokens.Colors.BrandPrimary
        }

    val BrandTint: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.BrandTint
            is TrackyTokens.ColorsDark -> c.BrandTint
            else -> TrackyTokens.Colors.BrandTint
        }

    val BrandDeep: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.BrandDeep
            is TrackyTokens.ColorsDark -> c.BrandDeep
            else -> TrackyTokens.Colors.BrandDeep
        }

    val Success: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Success
            is TrackyTokens.ColorsDark -> c.Success
            else -> TrackyTokens.Colors.Success
        }

    val Warning: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Warning
            is TrackyTokens.ColorsDark -> c.Warning
            else -> TrackyTokens.Colors.Warning
        }

    val Error: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Error
            is TrackyTokens.ColorsDark -> c.Error
            else -> TrackyTokens.Colors.Error
        }

    val Surface: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Surface
            is TrackyTokens.ColorsDark -> c.Surface
            else -> TrackyTokens.Colors.Surface
        }

    val Background: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Background
            is TrackyTokens.ColorsDark -> c.Background
            else -> TrackyTokens.Colors.Background
        }

    val Border: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Border
            is TrackyTokens.ColorsDark -> c.Border
            else -> TrackyTokens.Colors.Border
        }

    val TextPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.TextPrimary
            is TrackyTokens.ColorsDark -> c.TextPrimary
            else -> TrackyTokens.Colors.TextPrimary
        }

    val TextSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.TextSecondary
            is TrackyTokens.ColorsDark -> c.TextSecondary
            else -> TrackyTokens.Colors.TextSecondary
        }

    val TextTertiary: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.TextTertiary
            is TrackyTokens.ColorsDark -> c.TextTertiary
            else -> TrackyTokens.Colors.TextTertiary
        }

    val TextOnPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.TextOnPrimary
            is TrackyTokens.ColorsDark -> c.TextOnPrimary
            else -> TrackyTokens.Colors.TextOnPrimary
        }

    val Divider: Color
        @Composable
        @ReadOnlyComposable
        get() = when (val c = colors) {
            is TrackyTokens.Colors -> c.Divider
            is TrackyTokens.ColorsDark -> c.Divider
            else -> TrackyTokens.Colors.Divider
        }
}
