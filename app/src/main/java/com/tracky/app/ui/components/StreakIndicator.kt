package com.tracky.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracky.app.domain.model.StreakInfo
import com.tracky.app.domain.model.StreakStatus
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@Composable
fun StreakIndicator(
    streakInfo: StreakInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = false,
    onAnimationComplete: () -> Unit = {}
) {
    val status = streakInfo.status
    val count = streakInfo.count

    // ─────────────────────────────────────────────────────────────────────────
    // Animation State: Coin Flip (RotationY)
    // ─────────────────────────────────────────────────────────────────────────
    var rotation by remember { mutableStateOf(0f) }
    
    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            // Perform 360 degree flip
            rotation = 0f
            androidx.compose.animation.core.animate(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = tween(durationMillis = 600)
            ) { value, _ ->
                rotation = value
            }
            onAnimationComplete()
        }
    }

    val rotationY by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "streakRotation"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Colors based on Status (Updated: Grey for Inactive/Frozen, Deep Blue for Active)
    // ─────────────────────────────────────────────────────────────────────────
    val contentColor by animateColorAsState(
        targetValue = when (status) {
            StreakStatus.ACTIVE -> TrackyColors.BrandDeep
            else -> TrackyColors.TextTertiary
        },
        label = "streakColor"
    )

    val bgColor by animateColorAsState(
        targetValue = when (status) {
            StreakStatus.ACTIVE -> TrackyColors.BrandDeep.copy(alpha = 0.2f)
            else -> Color.Transparent
        },
        label = "streakBg"
    )

    // Fire Icon from SVG path data
    val fireIcon = remember<ImageVector> {
        ImageVector.Builder(
            name = "StreakFire",
            defaultWidth = 1024.dp,
            defaultHeight = 1024.dp,
            viewportWidth = 1024f,
            viewportHeight = 1024f
        ).path(
            fill = SolidColor(Color.Black), // Tinted later
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(834.1f, 469.2f)
            curveToRelative(-21.8f, -48.3f, -50.1f, -87.8f, -82.9f, -115.2f)
            lineToRelative(-29.1f, -26.7f)
            arcToRelative(8.09f, 8.09f, 0f, false, false, -13f, 3.3f)
            lineToRelative(-13f, 37.3f)
            curveToRelative(-8.1f, 23.4f, -23f, 47.3f, -44.1f, 70.8f)
            curveToRelative(-1.4f, 1.5f, -3f, 1.9f, -4.1f, 2f)
            curveToRelative(-1.1f, 0.1f, -2.8f, -0.1f, -4.3f, -1.5f)
            curveToRelative(-1.4f, -1.2f, -2.1f, -3f, -2f, -4.8f)
            curveToRelative(3.7f, -60.2f, -14.3f, -128.1f, -53.7f, -202f)
            curveToRelative(-39.2f, -73.6f, -84.5f, -121.5f, -141.1f, -154.9f)
            lineToRelative(-41.3f, -24.3f)
            arcToRelative(261.27f, 261.27f, 0f, false, false, -12f, 7.3f)
            lineToRelative(2.2f, 48f)
            curveToRelative(1.5f, 32.8f, -2.3f, 61.8f, -11.3f, 85.9f)
            curveToRelative(-11f, 29.5f, -26.8f, 56.9f, -47f, 81.5f)
            arcToRelative(295.64f, 295.64f, 0f, false, true, -47.5f, 46.1f)
            arcToRelative(352.6f, 352.6f, 0f, false, false, -100.3f, 121.5f)
            arcToRelative(347.75f, 347.75f, 0f, false, false, -14.2f, 140.8f)
            curveToRelative(0f, 47.2f, 9.3f, 92.9f, 27.7f, 136f)
            arcToRelative(349.4f, 349.4f, 0f, false, false, 75.5f, 110.9f)
            curveToRelative(32.4f, 32f, 70f, 57.2f, 111.9f, 74.7f)
            curveToRelative(44.3f, 18.5f, 90.3f, 27.7f, 137.8f, 27.7f)
            reflectiveCurveToRelative(93.5f, -9.2f, 136.9f, -27.3f)
            arcToRelative(348.6f, 348.6f, 0f, false, false, 111.9f, -74.7f)
            curveToRelative(32.4f, -32f, 57.8f, -69.4f, 75.5f, -110.9f)
            arcToRelative(344.2f, 344.2f, 0f, false, false, 27.7f, -136f)
            curveToRelative(0f, -48.8f, -10f, -96.2f, -29.9f, -140.9f)
            close()
        }.build()
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 12f * density
            }
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = fireIcon,
                contentDescription = "Streak",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            if (count > 0 || status != StreakStatus.NONE) {
                Text(
                    text = count.toString(),
                    color = if (status == StreakStatus.NONE) TrackyColors.TextTertiary else contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
