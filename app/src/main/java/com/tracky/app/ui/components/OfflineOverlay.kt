package com.tracky.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.blur
import android.os.Build

@Composable
fun OfflineOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Blurred background effect for Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(12.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(TrackyTokens.Spacing.XL)
            ) {
                Icon(
                    imageVector = Icons.Outlined.SentimentDissatisfied,
                    contentDescription = "Offline",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                
                TrackyText(
                    text = "You're offline",
                    style = TrackyTextStyle.HeadlineSmall,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                
                TrackyBodyText(
                    text = "Reconnect to the internet to continue tracking",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
