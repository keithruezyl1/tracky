package com.tracky.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens

/**
 * Tracky Base Card
 * 
 * Radius: 16dp
 * Padding: 16dp
 * Fill: Neutral/0
 * Border: 1dp Neutral/200
 */
@Composable
fun TrackyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = TrackyColors.Surface,
        contentColor = TrackyColors.TextPrimary
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = TrackyShapes.Large,
            colors = cardColors,
            border = BorderStroke(
                width = TrackyTokens.Sizes.BorderWidth,
                color = TrackyColors.Border
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(TrackyTokens.Spacing.CardPadding)
            ) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = TrackyShapes.Large,
            colors = cardColors,
            border = BorderStroke(
                width = TrackyTokens.Sizes.BorderWidth,
                color = TrackyColors.Border
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(TrackyTokens.Spacing.CardPadding)
            ) {
                content()
            }
        }
    }
}

/**
 * Tracky Info Card / Tinted Card
 * 
 * Fill: Brand/Tint
 * Text: Neutral/900
 */
@Composable
fun TrackyInfoCard(
    modifier: Modifier = Modifier,
    containerColor: Color = TrackyColors.BrandTint,
    borderColor: Color = TrackyColors.BrandPrimary.copy(alpha = 0.2f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = TrackyColors.TextPrimary
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = TrackyShapes.Large,
            colors = cardColors,
            border = BorderStroke(
                width = TrackyTokens.Sizes.BorderWidth,
                color = borderColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(TrackyTokens.Spacing.CardPadding)
            ) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = TrackyShapes.Large,
            colors = cardColors,
            border = BorderStroke(
                width = TrackyTokens.Sizes.BorderWidth,
                color = borderColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(TrackyTokens.Spacing.CardPadding)
            ) {
                content()
            }
        }
    }
}

/**
 * Tracky Entry Card for food/exercise entries
 * 
 * Card container with radius 16dp
 * Used in entry list rows
 */
@Composable
fun TrackyEntryCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = TrackyShapes.Large,
        colors = CardDefaults.cardColors(
            containerColor = TrackyColors.Surface,
            contentColor = TrackyColors.TextPrimary
        ),
        border = BorderStroke(
            width = TrackyTokens.Sizes.BorderWidth,
            color = TrackyColors.Border
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(TrackyTokens.Spacing.CardPadding)
        ) {
            content()
        }
    }
}
