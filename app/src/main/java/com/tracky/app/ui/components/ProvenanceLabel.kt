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
    val (label, dotColor) = when (source) {
        ProvenanceSource.AI_ESTIMATE -> "Estimated" to Color(0xFF5B9BD5) // Blue
        ProvenanceSource.USER_HISTORY -> "Saved" to Color(0xFF4CAF50)    // Green
        ProvenanceSource.DATASET -> "Saved" to Color(0xFF4CAF50)         // Green
        ProvenanceSource.USER_OVERRIDE -> "Manual" to Color(0xFFFFA726)  // Amber
        @Suppress("DEPRECATION")
        ProvenanceSource.USDA_FDC -> "Saved" to Color(0xFF4CAF50)       // Legacy
        @Suppress("DEPRECATION")
        ProvenanceSource.INTERNET -> "Estimated" to Color(0xFF5B9BD5)   // Legacy
        ProvenanceSource.UNRESOLVED -> "Unresolved" to Color(0xFFEF5350) // Red
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(dotColor.copy(alpha = 0.08f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = TrackyTypography.LabelSmall,
            color = dotColor
        )
    }
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
