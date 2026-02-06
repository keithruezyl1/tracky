package com.tracky.app.domain.model

/**
 * Domain model for Weight Entry
 */
data class WeightEntry(
    val id: Long = 0,
    val date: String,
    val weightKg: Float,
    val note: String?,
    val timestamp: Long,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Weight chart time range
 */
enum class WeightChartRange {
    DAY,
    WEEK,
    MONTH,
    ALL
}
