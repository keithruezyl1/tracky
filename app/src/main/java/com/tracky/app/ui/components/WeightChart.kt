package com.tracky.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.tracky.app.domain.model.WeightEntry
import com.tracky.app.ui.theme.TrackyColors
import com.patrykandpatrick.vico.compose.common.fill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Weight Chart Component
 * 
 * Displays weight progress over time using Vico chart library.
 * Uses blue gradient fill per design guidelines.
 */
@Composable
fun WeightChart(
    entries: List<WeightEntry>,
    targetWeightKg: Float,
    modifier: Modifier = Modifier
) {
    // Use entries content as the animation trigger key
    val entriesKey = remember(entries) { entries.map { it.id to it.weightKg }.hashCode() }
    
    // Model producer for chart data
    val modelProducer = remember { CartesianChartModelProducer() }
    
    // Animation state for Y-values
    val animationProgress = remember { Animatable(0f) }
    
    // Prepare chart data with animation - trigger on entries content change
    LaunchedEffect(entriesKey) {
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
    LaunchedEffect(entriesKey, animationProgress.value) {
        withContext(Dispatchers.Default) {
            if (entries.isEmpty()) {
                modelProducer.runTransaction {
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
                
                modelProducer.runTransaction {
                    lineSeries { series(animatedWeights) }
                }
            }
        }
    }
    
    // Blue area fill under the line - per design guidelines using BrandPrimary
    val areaFill = LineCartesianLayer.AreaFill.single(
        fill(TrackyColors.BrandPrimary.copy(alpha = 0.3f))
    )
    
    // Chart styling - line layer with blue gradient fill
    val lineLayer = rememberLineCartesianLayer(
        lineProvider = rememberLineCartesianLayerLineProvider(
             rememberLine(
                 fill = LineCartesianLayer.LineFill.single(fill(TrackyColors.BrandPrimary)),
                 areaFill = areaFill,
                 thickness = 2.dp
             )
        )
    )
    
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
            val sortedEntries = entries.sortedBy { it.timestamp }
            if (index in sortedEntries.indices) {
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

@Composable
fun rememberLineCartesianLayerLineProvider(
    line: com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.Line
): com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.LineProvider {
    return remember(line) {
        com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.LineProvider { _, _ -> line }
    }
}
