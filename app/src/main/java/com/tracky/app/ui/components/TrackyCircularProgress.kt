package com.tracky.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

@Composable
fun TrackyCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = TrackyColors.BrandPrimary,
    gradient: Brush? = null,
    trackColor: Color = TrackyColors.Neutral200.copy(alpha = 0.4f),
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "CircularProgressAnimation"
    )

    Canvas(modifier = modifier) {
        // Draw track
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Draw progress
        if (gradient != null) {
            drawArc(
                brush = gradient,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        } else {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun TrackyCircularMacroProgress(
    label: String,
    consumed: Float,
    target: Float,
    unit: String = "g",
    color: Color = TrackyColors.BrandPrimary,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) consumed / target else 0f
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            TrackyCircularProgress(
                progress = progress,
                modifier = Modifier.size(56.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
                strokeWidth = 6.dp
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${consumed.toInt()}/${target.toInt()}$unit",
                    style = TrackyTypography.LabelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 10.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Text(
            text = label,
            style = TrackyTypography.LabelSmall,
            color = TrackyColors.Neutral500,
            modifier = Modifier.padding(top = TrackyTokens.Spacing.XS)
        )
    }
}
