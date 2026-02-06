package com.tracky.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Tracky Text Styles
 * 
 * Enumeration of allowed text styles per tracky_designguidelines.md.
 * DO NOT introduce new styles outside this enum.
 */
enum class TrackyTextStyle {
    DisplayLarge,   // 28/32 - Screen titles
    HeadlineLarge,  // 22/28 - Section titles (H1)
    HeadlineMedium, // 18/24 - Card titles (H2)
    BodyLarge,      // 16/22 - Body
    BodyMedium,     // 14/20 - Body S
    LabelSmall,     // 12/16 - Caption
    LabelExtraSmall,// 11/14 - Overline
    Button          // 16/22 - Button text
}

/**
 * Convert TrackyTextStyle to actual TextStyle
 */
private fun TrackyTextStyle.toTextStyle(): TextStyle = when (this) {
    TrackyTextStyle.DisplayLarge -> TrackyTypography.DisplayLarge
    TrackyTextStyle.HeadlineLarge -> TrackyTypography.HeadlineLarge
    TrackyTextStyle.HeadlineMedium -> TrackyTypography.HeadlineMedium
    TrackyTextStyle.BodyLarge -> TrackyTypography.BodyLarge
    TrackyTextStyle.BodyMedium -> TrackyTypography.BodyMedium
    TrackyTextStyle.LabelSmall -> TrackyTypography.LabelSmall
    TrackyTextStyle.LabelExtraSmall -> TrackyTypography.LabelExtraSmall
    TrackyTextStyle.Button -> TrackyTypography.Button
}

/**
 * Tracky Text
 * 
 * Wrapper for Text that enforces the type scale.
 * Use this instead of raw Text() in feature screens to ensure compliance.
 */
@Composable
fun TrackyText(
    text: String,
    style: TrackyTextStyle,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextPrimary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.toTextStyle(),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// CONVENIENCE COMPOSABLES
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Screen title (Display L)
 */
@Composable
fun TrackyScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextPrimary
) {
    TrackyText(
        text = text,
        style = TrackyTextStyle.DisplayLarge,
        modifier = modifier,
        color = color
    )
}

/**
 * Section title (H1)
 */
@Composable
fun TrackySectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextPrimary
) {
    TrackyText(
        text = text,
        style = TrackyTextStyle.HeadlineLarge,
        modifier = modifier,
        color = color
    )
}

/**
 * Card title (H2)
 */
@Composable
fun TrackyCardTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextPrimary
) {
    TrackyText(
        text = text,
        style = TrackyTextStyle.HeadlineMedium,
        modifier = modifier,
        color = color
    )
}

/**
 * Body text
 */
@Composable
fun TrackyBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextPrimary,
    maxLines: Int = Int.MAX_VALUE
) {
    TrackyText(
        text = text,
        style = TrackyTextStyle.BodyLarge,
        modifier = modifier,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Small body text
 */
@Composable
fun TrackyBodySmall(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextSecondary,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null
) {
    TrackyText(
        text = text,
        style = TrackyTextStyle.BodyMedium,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Caption text
 */
@Composable
fun TrackyCaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextTertiary
) {
    TrackyText(
        text = text,
        style = TrackyTextStyle.LabelSmall,
        modifier = modifier,
        color = color
    )
}

/**
 * Overline text (uppercase with tracking)
 */
@Composable
fun TrackyOverline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.TextTertiary
) {
    TrackyText(
        text = text.uppercase(),
        style = TrackyTextStyle.LabelExtraSmall,
        modifier = modifier,
        color = color
    )
}
