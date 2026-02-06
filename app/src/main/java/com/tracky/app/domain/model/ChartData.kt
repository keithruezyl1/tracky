package com.tracky.app.domain.model

data class WeightChartState(
    val points: List<WeightChartPoint> = emptyList(),
    val minY: Float = 0f,
    val maxY: Float = 100f,
    val yAxisTicks: List<Float> = emptyList(),
    val minTimestamp: Long = 0L,
    val maxX: Float = 1f, 
    val xAxisTicks: List<Long> = emptyList()
)

data class WeightChartPoint(
    val x: Float,
    val y: Float,
    val timestamp: Long, // Store original timestamp for lookup
    val label: String
)
