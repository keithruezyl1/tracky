package com.tracky.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTypography
import com.tracky.app.ui.theme.TrackyTokens

enum class BadgeStyle {
    INDICATOR, // Blue, sparkles/info - for AI estimates
    SUCCESS,   // Green, check - for saved/verified
    WARNING,   // Amber, edit/info - for manual edits
    DANGER     // Red, warning/error - for unresolved
}

@Composable
fun TrackyBadge(
    text: String,
    style: BadgeStyle,
    modifier: Modifier = Modifier,
    compact: Boolean = true
) {
    val (icon, color) = when (style) {
        BadgeStyle.INDICATOR -> Icons.Outlined.Info to Color(0xFF5B9BD5)
        BadgeStyle.SUCCESS -> Icons.Outlined.Check to TrackyColors.Success
        BadgeStyle.WARNING -> Icons.Outlined.Edit to TrackyColors.Warning
        BadgeStyle.DANGER -> Icons.Outlined.Warning to TrackyColors.Error
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(
                horizontal = if (compact) 6.dp else 8.dp,
                vertical = if (compact) 2.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(if (compact) 12.dp else 14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = if (compact) TrackyTypography.LabelSmall else TrackyTypography.BodyMedium,
            color = color
        )
    }
}
