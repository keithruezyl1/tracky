package com.tracky.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableRow(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val actionWidth = 80.dp
    val actionWidthPx = with(density) { actionWidth.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) 
    ) {
        // Validation/Delete Button (Background)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(actionWidth)
                .fillMaxHeight()
                .padding(vertical = 4.dp) // Little margin to separate from other items
                .clickable {
                    scope.launch { 
                        offsetX.animateTo(0f)
                        onDelete() 
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = TrackyColors.Error
            )
        }
        
        // Content (Foreground)
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .background(TrackyColors.Background) // Ensure opacity
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // Snap logic
                            val target = if (offsetX.value < -actionWidthPx / 2) -actionWidthPx else 0f
                            scope.launch { offsetX.animateTo(target) }
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f) }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            // Drag logic (limit to -actionWidthPx)
                            val newOffset = (offsetX.value + dragAmount).coerceIn(-actionWidthPx, 0f)
                            scope.launch { offsetX.snapTo(newOffset) }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
