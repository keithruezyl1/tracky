package com.tracky.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tracky Design System Tokens
 * 
 * All UI must use these tokens exclusively. No ad-hoc hex colors, 
 * spacing values, or corner radii outside these definitions.
 */
object TrackyTokens {

    // ─────────────────────────────────────────────────────────────────────────
    // COLOR TOKENS (from tracky_designguidelines.md)
    // Usage distribution: Neutrals 80%, Brand Blue 15%, Accents 5%
    // ─────────────────────────────────────────────────────────────────────────

    object Colors {
        // Neutrals (Light)
        val Neutral0 = Color(0xFFFFFFFF)   // Surface
        val Neutral50 = Color(0xFFF7F8FA)  // App background
        val Neutral100 = Color(0xFFEEF1F5) // Subtle surface, separators
        val Neutral200 = Color(0xFFE2E6EC) // Borders/dividers
        val Neutral500 = Color(0xFF8B93A3) // Tertiary text
        val Neutral700 = Color(0xFF556070) // Secondary text
        val Neutral900 = Color(0xFF0B0D11) // Primary text

        // Brand
        val BrandPrimary = Color(0xFF2E6BB5)  // Primary CTAs, selected states
        val BrandTint = Color(0xFFEAF2FF)     // Info surface, header strips
        val BrandDeep = Color(0xFF1E4D86)     // Pressed/active variants

        // Semantic accents
        val Success = Color(0xFF34C759)
        val Warning = Color(0xFFFF9500)
        val Error = Color(0xFFFF3B30)

        // Derived colors for specific use cases (Light)
        val Surface = Neutral0
        val Background = Neutral50
        val Border = Neutral200
        val TextPrimary = Neutral900
        val TextSecondary = Neutral700
        val TextTertiary = Neutral500
        val TextOnPrimary = Neutral0
        val Divider = Neutral200
    }

    /**
     * Dark mode color tokens (from tracky_designguidelines.md section 3.X)
     * Usage distribution: Neutrals 85%, Brand Blue 12%, Accents 3%
     */
    object ColorsDark {
        // Neutrals (Dark) - using same names as light mode for interchangeability
        val Neutral0 = Color(0xFF121722)   // Surface / cards (was NeutralD100)
        val Neutral50 = Color(0xFF0B0D11)  // App background base (was NeutralD0)
        val Neutral100 = Color(0xFF171D2A) // Subtle surface, separators (was NeutralD150)
        val Neutral200 = Color(0xFF2A3344) // Borders/dividers (was NeutralD300)
        val Neutral500 = Color(0xFF98A2B3) // Tertiary text (was NeutralD500)
        val Neutral700 = Color(0xFFC6CDD8) // Secondary text (was NeutralD700)
        val Neutral900 = Color(0xFFF5F7FA) // Primary text (was NeutralD900)

        // Brand (Dark) - BrandPrimary and BrandDeep same as light
        val BrandPrimary = Color(0xFF2E6BB5)  // Same as light; used sparingly
        val BrandTint = Color(0xFF0F2542)     // Tinted info surface for dark mode (was BrandTintDark)
        val BrandDeep = Color(0xFF1E4D86)     // Pressed/active variants

        // Semantic accents (Dark) - Same as light
        val Success = Color(0xFF34C759)
        val Warning = Color(0xFFFF9500)
        val Error = Color(0xFFFF3B30)

        // Derived colors for specific use cases (Dark)
        val Surface = Neutral0
        val Background = Neutral50
        val Border = Neutral200
        val TextPrimary = Neutral900
        val TextSecondary = Neutral700
        val TextTertiary = Neutral500
        val TextOnPrimary = Neutral900
        val Divider = Neutral200
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPACING TOKENS (8-pt grid system)
    // Use only these values. No off-grid spacing.
    // ─────────────────────────────────────────────────────────────────────────

    object Spacing {
        val XXS: Dp = 4.dp
        val XS: Dp = 8.dp
        val S: Dp = 12.dp
        val M: Dp = 16.dp    // Default screen/card padding
        val L: Dp = 24.dp    // Section spacing
        val XL: Dp = 32.dp
        val XXL: Dp = 40.dp
        val XXXL: Dp = 48.dp

        // Semantic spacing aliases
        val ScreenPadding: Dp = M     // 16
        val CardPadding: Dp = M       // 16
        val SectionSpacing: Dp = L    // 24
        val DenseListSpacing: Dp = S  // 12
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RADIUS TOKENS
    // Use only these values. No random corner rounding.
    // ─────────────────────────────────────────────────────────────────────────

    object Radii {
        val XS: Dp = 8.dp   // Small elements
        val S: Dp = 12.dp   // Inputs, small cards
        val M: Dp = 14.dp   // Buttons default
        val L: Dp = 16.dp   // Cards default
        val XL: Dp = 24.dp  // Bottom sheets top corners
        val Pill: Dp = 999.dp // Chips, segmented control
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SIZE TOKENS
    // Component heights and standard sizes
    // ─────────────────────────────────────────────────────────────────────────

    object Sizes {
        // Button heights
        val ButtonHeight: Dp = 48.dp
        
        // Input heights
        val InputHeight: Dp = 48.dp
        
        // Chip heights
        val ChipHeight: Dp = 32.dp
        val ChipHeightSmall: Dp = 28.dp
        
        // Touch targets (accessibility minimum)
        val MinTouchTarget: Dp = 48.dp
        
        // Icon sizes
        val IconSmall: Dp = 16.dp
        val IconMedium: Dp = 20.dp
        val IconLarge: Dp = 24.dp
        
        // Border width
        val BorderWidth: Dp = 1.dp
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ANIMATION TOKENS
    // Duration and easing values for consistent motion
    // ─────────────────────────────────────────────────────────────────────────

    object Animation {
        // Durations in milliseconds
        const val DurationMicroFast: Int = 150
        const val DurationMicro: Int = 200
        const val DurationMicroSlow: Int = 250
        const val DurationNavigation: Int = 300
        const val DurationNavigationSlow: Int = 350
    }
}
