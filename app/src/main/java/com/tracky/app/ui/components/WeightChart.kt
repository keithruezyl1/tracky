package com.tracky.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.tracky.app.domain.model.WeightEntry
import com.tracky.app.ui.theme.TrackyColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

/**
 * Weight Chart Component
 * 
 * Displays weight progress over time using Vico chart library.
 * Features:
 * - Gradient-filled area chart
 * - Light/dark mode support
 * - Entry animation (lines animate from bottom on every screen visit)
 */
@Composable
fun WeightChart(
    entries: List<WeightEntry>,
    targetWeightKg: Float,
    modifier: Modifier = Modifier
) {
    // Generate unique key on every composition to trigger animation
    val animationKey = remember { UUID.randomUUID().toString() }
    
    // Model producer for chart data
    val modelProducer = remember { CartesianChartModelProducer.build() }
    
    // Animation state for Y-values
    val animationProgress = remember { Animatable(0f) }
    
    // Prepare chart data with animation
    LaunchedEffect(entries, animationKey) {
        // Reset animation
        animationProgress.snapTo(0f)
        
        if (entries.isNotEmpty()) {
            // Start animation
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350)
            )
        }
    }
    
    // Update model when entries or animation progress changes
    LaunchedEffect(entries, animationProgress.value) {
        withContext(Dispatchers.Default) {
            if (entries.isEmpty()) {
                modelProducer.tryRunTransaction {
                    lineSeries { series(emptyList<Float>()) }
                }
            } else {
                // Sort entries by timestamp
                val sortedEntries = entries.sortedBy { it.timestamp }
                
                // Animate from 0 to actual values
                val minWeight = sortedEntries.minOf { it.weightKg }
                val animatedWeights = sortedEntries.map { entry ->
                    minWeight + (entry.weightKg - minWeight) * animationProgress.value
                }
                
                modelProducer.tryRunTransaction {
                    lineSeries { series(animatedWeights) }
                }
            }
        }
    }
    
    // Chart styling - simple line layer with gradient fill
    val lineLayer = rememberLineCartesianLayer()
    
    // Axis styling
    val startAxis = rememberStartAxis(
        label = rememberTextComponent(
            color = TrackyColors.TextTertiary,
            textSize = 12.sp,
            margins = Dimensions.of(end = 8.dp)
        ),
        guideline = rememberLineComponent(
            color = TrackyColors.Border,
            thickness = 1.dp
        )
    )
    
    val bottomAxis = rememberBottomAxis(
        label = rememberTextComponent(
            color = TrackyColors.TextTertiary,
            textSize = 12.sp,
            margins = Dimensions.of(top = 8.dp)
        ),
        valueFormatter = { value, _, _ ->
            // Format date labels
            if (entries.isEmpty()) return@rememberBottomAxis ""
            val index = value.toInt()
            if (index in entries.indices) {
                val sortedEntries = entries.sortedBy { it.timestamp }
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = dateFormat.parse(sortedEntries[index].date)
                    val labelFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                    date?.let { labelFormat.format(it) } ?: ""
                } catch (e: Exception) {
                    ""
                }
            } else {
                ""
            }
        }
    )
    
    val chart = rememberCartesianChart(
        lineLayer,
        startAxis = startAxis,
        bottomAxis = bottomAxis
    )
    
    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
