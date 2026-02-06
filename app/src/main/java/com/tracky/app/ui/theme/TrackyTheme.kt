package com.tracky.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Provides access to Tracky spacing tokens throughout the composition.
 */
val LocalTrackySpacing = staticCompositionLocalOf { TrackyTokens.Spacing }

/**
 * Provides access to Tracky radii tokens throughout the composition.
 */
val LocalTrackyRadii = staticCompositionLocalOf { TrackyTokens.Radii }

/**
 * Provides access to Tracky color tokens throughout the composition.
 * Using Any to accommodate both Colors and ColorsDark object types.
 */
val LocalTrackyColors = staticCompositionLocalOf<Any> { TrackyTokens.Colors }

// ─────────────────────────────────────────────────────────────────────────────
// COLOR SCHEMES (MAPPING TO MATERIAL 3)
// ─────────────────────────────────────────────────────────────────────────────

private val TrackyLightColorScheme = lightColorScheme(
    primary = TrackyTokens.Colors.BrandPrimary,
    onPrimary = TrackyTokens.Colors.TextOnPrimary,
    primaryContainer = TrackyTokens.Colors.BrandTint,
    onPrimaryContainer = TrackyTokens.Colors.BrandDeep,
    
    secondary = TrackyTokens.Colors.BrandPrimary,
    onSecondary = TrackyTokens.Colors.TextOnPrimary,
    secondaryContainer = TrackyTokens.Colors.BrandTint,
    onSecondaryContainer = TrackyTokens.Colors.BrandDeep,
    
    tertiary = TrackyTokens.Colors.Neutral500,
    onTertiary = TrackyTokens.Colors.Neutral0,
    tertiaryContainer = TrackyTokens.Colors.Neutral100,
    onTertiaryContainer = TrackyTokens.Colors.Neutral900,
    
    background = TrackyTokens.Colors.Background,
    onBackground = TrackyTokens.Colors.TextPrimary,
    surface = TrackyTokens.Colors.Surface,
    onSurface = TrackyTokens.Colors.TextPrimary,
    surfaceVariant = TrackyTokens.Colors.Neutral100,
    onSurfaceVariant = TrackyTokens.Colors.TextSecondary,
    
    outline = TrackyTokens.Colors.Divider,
    outlineVariant = TrackyTokens.Colors.Neutral200,
    
    error = TrackyTokens.Colors.Error,
    onError = TrackyTokens.Colors.Neutral0,
    errorContainer = TrackyTokens.Colors.Error.copy(alpha = 0.1f),
    onErrorContainer = TrackyTokens.Colors.Error,

    // Inverse
    inverseSurface = TrackyTokens.Colors.Neutral900,
    inverseOnSurface = TrackyTokens.Colors.Neutral0,
    inversePrimary = TrackyTokens.Colors.BrandTint,

    // Scrim
    scrim = TrackyTokens.Colors.Neutral900.copy(alpha = 0.32f)
)

private val TrackyDarkColorScheme = darkColorScheme(
    primary = TrackyTokens.ColorsDark.BrandPrimary,
    onPrimary = TrackyTokens.ColorsDark.Neutral50,
    primaryContainer = TrackyTokens.ColorsDark.BrandTint,
    onPrimaryContainer = TrackyTokens.ColorsDark.BrandPrimary,
    
    secondary = TrackyTokens.ColorsDark.BrandPrimary,
    onSecondary = TrackyTokens.ColorsDark.Neutral50,
    secondaryContainer = TrackyTokens.ColorsDark.BrandTint,
    onSecondaryContainer = TrackyTokens.ColorsDark.BrandPrimary,
    
    tertiary = TrackyTokens.ColorsDark.Neutral500,
    onTertiary = TrackyTokens.ColorsDark.Neutral50,
    tertiaryContainer = TrackyTokens.ColorsDark.Neutral900,
    onTertiaryContainer = TrackyTokens.ColorsDark.Neutral100,
    
    background = TrackyTokens.ColorsDark.Background,
    onBackground = TrackyTokens.ColorsDark.TextPrimary,
    surface = TrackyTokens.ColorsDark.Surface,
    onSurface = TrackyTokens.ColorsDark.TextPrimary,
    surfaceVariant = TrackyTokens.ColorsDark.Neutral900,
    onSurfaceVariant = TrackyTokens.ColorsDark.TextSecondary,
    
    outline = TrackyTokens.ColorsDark.Divider,
    outlineVariant = TrackyTokens.ColorsDark.Neutral200,
    
    error = TrackyTokens.ColorsDark.Error,
    onError = TrackyTokens.ColorsDark.Neutral50,
    errorContainer = TrackyTokens.ColorsDark.Error.copy(alpha = 0.1f),
    onErrorContainer = TrackyTokens.ColorsDark.Error,

    // Inverse
    inverseSurface = TrackyTokens.ColorsDark.Neutral900,
    inverseOnSurface = TrackyTokens.ColorsDark.Neutral50,
    inversePrimary = TrackyTokens.ColorsDark.BrandTint,

    // Scrim
    scrim = TrackyTokens.Colors.Neutral900.copy(alpha = 0.50f)
)

// ─────────────────────────────────────────────────────────────────────────────
// THEME COMPOSABLE
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TrackyTheme(
    useDarkTheme: Boolean = false,  // Default to light mode
    content: @Composable () -> Unit
) {
    // Select color scheme based on dark mode preference
    val colorScheme = if (useDarkTheme) TrackyDarkColorScheme else TrackyLightColorScheme
    
    val colors: Any = if (useDarkTheme) TrackyTokens.ColorsDark else TrackyTokens.Colors

    // Update system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            if (useDarkTheme) {
                window.statusBarColor = TrackyTokens.ColorsDark.Neutral50.toArgb()
                window.navigationBarColor = TrackyTokens.ColorsDark.Neutral0.toArgb()
                
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            } else {
                window.statusBarColor = TrackyTokens.Colors.Neutral50.toArgb()
                window.navigationBarColor = TrackyTokens.Colors.Neutral0.toArgb()
                
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = true
                    isAppearanceLightNavigationBars = true
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalTrackySpacing provides TrackyTokens.Spacing,
        LocalTrackyRadii provides TrackyTokens.Radii,
        LocalTrackyColors provides colors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TrackyTypography.MaterialTypography,
            shapes = TrackyShapes.MaterialShapes,
            content = content
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// THEME ACCESSORS
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Access Tracky spacing tokens.
 */
val TrackySpacing: TrackyTokens.Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalTrackySpacing.current

/**
 * Access Tracky radii tokens.
 */
val TrackyRadii: TrackyTokens.Radii
    @Composable
    @ReadOnlyComposable
    get() = LocalTrackyRadii.current
