package com.tracky.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * Tracky Shapes System
 * 
 * Based on radius tokens from tracky_designguidelines.md.
 * Use only these shapes for consistency.
 */
object TrackyShapes {

    // ─────────────────────────────────────────────────────────────────────────
    // SHAPE DEFINITIONS (using TrackyTokens.Radii)
    // ─────────────────────────────────────────────────────────────────────────

    // XS: 8dp - Small elements
    val ExtraSmall = RoundedCornerShape(TrackyTokens.Radii.XS)

    // S: 12dp - Inputs, small cards
    val Small = RoundedCornerShape(TrackyTokens.Radii.S)

    // M: 14dp - Buttons default
    val Medium = RoundedCornerShape(TrackyTokens.Radii.M)

    // L: 16dp - Cards default
    val Large = RoundedCornerShape(TrackyTokens.Radii.L)

    // XL: 24dp - Bottom sheets top corners
    val ExtraLarge = RoundedCornerShape(TrackyTokens.Radii.XL)

    // Pill: 999dp - Chips, segmented control
    val Pill = RoundedCornerShape(TrackyTokens.Radii.Pill)

    // Bottom sheet shape (only top corners rounded)
    val BottomSheet = RoundedCornerShape(
        topStart = TrackyTokens.Radii.XL,
        topEnd = TrackyTokens.Radii.XL,
        bottomStart = TrackyTokens.Radii.XS, // Minimal rounding at bottom
        bottomEnd = TrackyTokens.Radii.XS
    )

    // ─────────────────────────────────────────────────────────────────────────
    // MATERIAL 3 SHAPES MAPPING
    // ─────────────────────────────────────────────────────────────────────────

    val MaterialShapes = Shapes(
        extraSmall = ExtraSmall,  // 8dp
        small = Small,            // 12dp
        medium = Medium,          // 14dp
        large = Large,            // 16dp
        extraLarge = ExtraLarge   // 24dp
    )
}
