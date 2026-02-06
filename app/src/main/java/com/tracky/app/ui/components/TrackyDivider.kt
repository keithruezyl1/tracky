package com.tracky.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tracky.app.ui.theme.TrackyTokens

/**
 * Tracky Horizontal Divider
 * 
 * Border thickness: 1dp
 * Color: Neutral/200
 */
@Composable
fun TrackyDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.M),
        thickness = TrackyTokens.Sizes.BorderWidth,
        color = TrackyTokens.Colors.Divider
    )
}

/**
 * Tracky Section Divider
 * 
 * Divider with standard vertical spacing for sections.
 */
@Composable
fun TrackySectionDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.M),
        thickness = TrackyTokens.Sizes.BorderWidth,
        color = TrackyTokens.Colors.Divider
    )
}

/**
 * Tracky List Divider
 * 
 * Divider with dense spacing for list items.
 */
@Composable
fun TrackyListDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.S),
        thickness = TrackyTokens.Sizes.BorderWidth,
        color = TrackyTokens.Colors.Divider
    )
}
