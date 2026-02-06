package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DraftExerciseItem(
    val activity: String,
    val durationMinutes: Int,
    val metValue: Float,
    val caloriesBurned: Int,
    val intensity: ExerciseIntensity,
    val resolved: Boolean = true
)
