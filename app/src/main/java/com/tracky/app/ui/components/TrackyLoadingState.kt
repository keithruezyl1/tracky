package com.tracky.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens

/**
 * Tracky Loading Indicator
 * 
 * Circular progress indicator with brand color.
 */
@Composable
fun TrackyLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = TrackyColors.BrandPrimary,
        strokeWidth = strokeWidth,
        trackColor = TrackyColors.Neutral100
    )
}

/**
 * Tracky Full Screen Loading
 */
@Composable
fun TrackyFullScreenLoading(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TrackyColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrackyLoadingIndicator()
        
        if (message != null) {
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            TrackyBodySmall(
                text = message,
                color = TrackyColors.TextSecondary
            )
        }
    }
}

/**
 * Tracky Shimmer Effect
 * 
 * For "Drafting..." loading states.
 */
@Composable
fun TrackyShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            TrackyColors.Neutral100,
            TrackyColors.Neutral50,
            TrackyColors.Neutral100
        ),
        start = Offset(shimmerTranslate - 200, 0f),
        end = Offset(shimmerTranslate, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(TrackyShapes.Small)
            .background(shimmerBrush)
    )
}

/**
 * Tracky Shimmer Card
 * 
 * Loading placeholder for cards.
 */
@Composable
fun TrackyShimmerCard(
    modifier: Modifier = Modifier
) {
    TrackyCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(TrackyTokens.Spacing.XS)
        ) {
            TrackyShimmerBox(height = 16.dp, modifier = Modifier.fillMaxWidth(0.6f))
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
            TrackyShimmerBox(height = 12.dp)
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
            TrackyShimmerBox(height = 12.dp, modifier = Modifier.fillMaxWidth(0.8f))
        }
    }
}

/**
 * Tracky Drafting State
 * 
 * Shows a pulsing/shimmer state while AI is drafting.
 */
@Composable
fun TrackyDraftingState(
    modifier: Modifier = Modifier
) {
    TrackyCard(modifier = modifier) {
        Column {
            TrackyBodySmall(
                text = remember { listOf("Analyzing...", "Calculating...").random() },
                color = TrackyColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
            TrackyShimmerBox(height = 16.dp)
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
            TrackyShimmerBox(height = 12.dp, modifier = Modifier.fillMaxWidth(0.7f))
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
            TrackyShimmerBox(height = 12.dp, modifier = Modifier.fillMaxWidth(0.5f))
        }
    }
}
