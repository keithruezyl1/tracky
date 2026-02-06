package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for Exercise Item
 */
@Serializable
data class ExerciseItem(
    val id: Long = 0,
    val activityName: String,
    val durationMinutes: Int,
    val metValue: Float,
    val caloriesBurned: Int,
    val intensity: ExerciseIntensity?,
    val provenance: Provenance,
    val displayOrder: Int
)
