package com.tracky.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tracky Typography System
 * 
 * Based on tracky_designguidelines.md type scale.
 * Use system font (Roboto) for UI body.
 * 
 * DO NOT introduce new text styles outside this scale.
 */
object TrackyTypography {

    // Font family - system font (Roboto on Android)
    private val DefaultFontFamily = FontFamily.Default

    // ─────────────────────────────────────────────────────────────────────────
    // TYPE SCALE (from tracky_designguidelines.md)
    // Format: fontSize/lineHeight
    // ─────────────────────────────────────────────────────────────────────────

    // Display L: 28/32 (screen titles)
    val DisplayLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

    // H1: 22/28 (section titles)
    val HeadlineLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    // H2: 18/24 (card titles)
    val HeadlineMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    // Body: 16/22
    val BodyLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    // Body S: 14/20
    val BodyMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

    // Caption: 12/16
    val LabelSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )

    // Overline: 11/14 (tracking +6% = ~0.66sp for 11sp)
    val LabelExtraSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.66.sp
    )

    // Button text
    val Button = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    // ─────────────────────────────────────────────────────────────────────────
    // MATERIAL 3 TYPOGRAPHY MAPPING
    // ─────────────────────────────────────────────────────────────────────────

    val MaterialTypography = Typography(
        // Display styles
        displayLarge = DisplayLarge,
        displayMedium = DisplayLarge, // Use Display L for both
        displaySmall = HeadlineLarge,

        // Headline styles
        headlineLarge = HeadlineLarge,
        headlineMedium = HeadlineMedium,
        headlineSmall = HeadlineMedium,

        // Title styles (map to our headings)
        titleLarge = HeadlineLarge,
        titleMedium = HeadlineMedium,
        titleSmall = BodyLarge.copy(fontWeight = FontWeight.SemiBold),

        // Body styles
        bodyLarge = BodyLarge,
        bodyMedium = BodyMedium,
        bodySmall = LabelSmall,

        // Label styles
        labelLarge = Button,
        labelMedium = BodyMedium.copy(fontWeight = FontWeight.Medium),
        labelSmall = LabelSmall
    )
}
