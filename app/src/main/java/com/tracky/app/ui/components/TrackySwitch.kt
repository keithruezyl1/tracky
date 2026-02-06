package com.tracky.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import kotlin.math.roundToInt

/**
 * TrackySwitch - Custom switch component following Tracky design guidelines
 * 
 * Design specs:
 * - Height: 32dp
 * - Width: 52dp
 * - Thumb size: 28dp
 * - Animation: 200ms (micro duration)
 * - Colors: BrandPrimary when enabled, Neutral200 when disabled
 */
@Composable
fun TrackySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Animate thumb position
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = TrackyTokens.Animation.DurationMicro),
        label = "thumb_offset"
    )
    
    // Colors based on state
    val trackColor = when {
        !enabled -> TrackyColors.Neutral200.copy(alpha = 0.5f)
        checked -> TrackyColors.BrandPrimary
        else -> TrackyColors.Neutral200
    }
    
    val thumbColor = TrackyColors.Neutral0
    
    Box(
        modifier = modifier
            .width(52.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .size(28.dp)
                .offset { IntOffset((thumbOffset * 20.dp.toPx()).roundToInt(), 0) }
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}
