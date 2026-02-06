package com.tracky.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur

@Composable
fun SuccessOverlay(
    visible: Boolean,
    onAnimationFinished: () -> Unit
) {
    if (!visible) return

    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.5f) }

    LaunchedEffect(visible) {
        // Fade in background + Scale up check
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
        scaleAnim.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        )
        
        // Slight scale down bounce
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 150)
        )

        // Hold
        delay(800)

        // Fade out
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300)
        )
        
        onAnimationFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // blurred background layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f * alphaAnim.value))
                .then(if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) 
                    Modifier.blur((10 * alphaAnim.value).dp) else Modifier)
        )

        // Foreground content (check icon + text)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Ripple 1
                val ripple1Scale = remember { Animatable(1f) }
                val ripple1Alpha = remember { Animatable(0.5f) }
                
                // Ripple 2
                val ripple2Scale = remember { Animatable(1f) }
                val ripple2Alpha = remember { Animatable(0.3f) }

                LaunchedEffect(visible) {
                    if (visible) {
                         // Delay slightly to start ripples after the pop
                        delay(100)
                        launch {
                            ripple1Scale.animateTo(1.5f, tween(500))
                        }
                        launch {
                            ripple1Alpha.animateTo(0f, tween(500))
                        }
                        
                        delay(100)
                        launch {
                            ripple2Scale.animateTo(1.8f, tween(500))
                        }
                        launch {
                            ripple2Alpha.animateTo(0f, tween(500))
                        }
                    }
                }

                if (visible) {
                    // Ripple Circle 2
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(ripple2Scale.value)
                            .alpha(ripple2Alpha.value),
                        shape = CircleShape,
                        color = com.tracky.app.ui.theme.TrackyColors.Success
                    ) {}
                    
                   // Ripple Circle 1
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(ripple1Scale.value)
                            .alpha(ripple1Alpha.value),
                        shape = CircleShape,
                        color = com.tracky.app.ui.theme.TrackyColors.Success
                    ) {}
                }

                // Main Circle
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    // Green circle
                    color = com.tracky.app.ui.theme.TrackyColors.Success
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            // White check
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(com.tracky.app.ui.theme.TrackyTokens.Spacing.XL))
            
            TrackyScreenTitle(
                text = "Entry logged!",
                color = Color.White
            )
        }
    }
}
