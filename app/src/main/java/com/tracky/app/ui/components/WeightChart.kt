package com.tracky.app.ui.components

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
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.toDynamicShader
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.tracky.app.domain.model.WeightChartState
import com.tracky.app.ui.theme.TrackyColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Weight Chart Component - Purely Data Driven
 * 
 * Displays weight progress using pre-calculated state.
 */
@Composable
fun WeightChart(
    chartState: WeightChartState,
    modifier: Modifier = Modifier
) {
    // Model producer for chart data
    val modelProducer = remember { CartesianChartModelProducer() }
    
    // Animation removed to ensure data correctness first

    
    // Use points content as key
    val pointsKey = remember(chartState.points) { chartState.points.hashCode() }

    LaunchedEffect(pointsKey) {
        withContext(Dispatchers.Default) {
             if (chartState.points.isNotEmpty()) {
                val xValues = chartState.points.map { it.x }
                val yValues = chartState.points.map { it.y }
                
                modelProducer.runTransaction {
                    lineSeries { 
                        series(x = xValues, y = yValues)
                    }
                }
            }
        }
    }
    
    val primaryColor = TrackyColors.BrandPrimary

    val areaFill = remember(primaryColor) {
        LineCartesianLayer.AreaFill.single(
            fill(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.4f),
                        primaryColor.copy(alpha = 0.0f)
                    )
                ).toDynamicShader()
            )
        )
    }
    
    val shapeComponent = rememberShapeComponent(
        shape = Shape.Pill,
        color = primaryColor,
        margins = Dimensions.of(2.dp)
    )
    val point = remember(shapeComponent) {
        com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.Point(shapeComponent)
    }

    // Dynamic Axis Overrides from State
    // We Add +/- 0.5 padding to X to ensure the integer-indexed points (0, 1, 2)
    // are plotted in the CENTER of the chart area, not on the edges.
    val axisValueOverrider = remember(chartState) {
        AxisValueOverrider.fixed(
            minY = chartState.minY.toDouble(),
            maxY = chartState.maxY.toDouble(),
            minX = -0.5, 
            maxX = chartState.maxX.toDouble() + 0.5
        )
    }

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = rememberLineCartesianLayerLineProvider(
             rememberLine(
                 fill = LineCartesianLayer.LineFill.single(fill(primaryColor)),
                 areaFill = areaFill,
                 thickness = 3.dp,
                 pointProvider = com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.PointProvider.single(point)
             )
        ),
        axisValueOverrider = axisValueOverrider
    )
    
    val startAxis = rememberStartAxis(
        label = rememberTextComponent(
            color = TrackyColors.TextTertiary,
            textSize = 12.sp,
            margins = Dimensions.of(end = 8.dp)
        ),
        guideline = rememberLineComponent(
            color = TrackyColors.Border,
            thickness = 1.dp
        ),
         valueFormatter = { value, _, _ -> 
            value.toInt().toString()
        }
    )
    
    val bottomAxis = rememberBottomAxis(
        label = rememberTextComponent(
            color = TrackyColors.TextTertiary,
            textSize = 12.sp,
            margins = Dimensions.of(top = 8.dp)
        ),
        valueFormatter = { value, _, _ ->
            if (chartState.points.isEmpty()) return@rememberBottomAxis ""
            try {
                // With segmented layout, value should be close to index
                val index = kotlin.math.round(value).toInt()
                if (kotlin.math.abs(value - index) < 0.2 && index in chartState.points.indices) {
                   val timestamp = chartState.points[index].timestamp
                   val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                   dateFormat.format(timestamp)
                } else {
                   ""
                }
            } catch (e: Exception) {
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
