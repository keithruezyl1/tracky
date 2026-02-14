package com.tracky.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Provenance display label for food and exercise items.
 *
 * Maps provenance source to human-readable labels:
 * - AI_ESTIMATE → "Estimated" (blue-ish)
 * - USER_HISTORY / DATASET → "Saved" (green)
 * - USER_OVERRIDE → "Manual" (amber)
 * - UNRESOLVED → "Unresolved" (red)
 */
@Composable
fun ProvenanceLabel(
    source: ProvenanceSource,
    modifier: Modifier = Modifier,
    compact: Boolean = true
) {
    val style = when (source) {
        ProvenanceSource.AI_ESTIMATE -> BadgeStyle.INDICATOR
        ProvenanceSource.USER_HISTORY -> BadgeStyle.SUCCESS
        ProvenanceSource.DATASET -> BadgeStyle.SUCCESS
        ProvenanceSource.USER_OVERRIDE -> BadgeStyle.WARNING
        @Suppress("DEPRECATION")
        ProvenanceSource.USDA_FDC -> BadgeStyle.SUCCESS
        @Suppress("DEPRECATION")
        ProvenanceSource.INTERNET -> BadgeStyle.INDICATOR
        ProvenanceSource.UNRESOLVED -> BadgeStyle.DANGER
    }

    TrackyBadge(
        text = source.displayLabel(),
        style = style,
        modifier = modifier,
        compact = compact
    )
}

/**
 * Get the user-facing label for a provenance source.
 */
fun ProvenanceSource.displayLabel(): String = when (this) {
    ProvenanceSource.AI_ESTIMATE -> "Estimated"
    ProvenanceSource.USER_HISTORY, ProvenanceSource.DATASET -> "Saved"
    ProvenanceSource.USER_OVERRIDE -> "Manual"
    @Suppress("DEPRECATION")
    ProvenanceSource.USDA_FDC -> "Saved"
    @Suppress("DEPRECATION")
    ProvenanceSource.INTERNET -> "Estimated"
    ProvenanceSource.UNRESOLVED -> "Unresolved"
}
