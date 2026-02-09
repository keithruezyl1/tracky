package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for Exercise Entry
 */
data class ExerciseEntry(
    val id: Long = 0,
    val date: String,
    val time: String,
    val timestamp: Long,
    val items: List<ExerciseItem>,
    val totalCalories: Float,
    val totalDurationMinutes: Int,
    val userWeightKg: Float,
    val originalInput: String?,
    val createdAt: Long,
    val updatedAt: Long
)




