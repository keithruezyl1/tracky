package com.tracky.app.domain.model

/**
 * Domain model for Exercise Entry
 */
data class ExerciseEntry(
    val id: Long = 0,
    val date: String,
    val time: String,
    val timestamp: Long,
    val activityName: String,
    val durationMinutes: Int,
    val metValue: Float,
    val userWeightKg: Float,
    val caloriesBurned: Int,
    val intensity: ExerciseIntensity?,
    val originalInput: String?,
    val provenance: Provenance,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        /**
         * Calculate calories burned using MET formula:
         * kcal = MET × weight_kg × hours
         */
        fun calculateCaloriesBurned(
            metValue: Float,
            weightKg: Float,
            durationMinutes: Int
        ): Int {
            val hours = durationMinutes / 60f
            return (metValue * weightKg * hours).toInt()
        }
    }
}

/**
 * Exercise intensity levels
 */
enum class ExerciseIntensity(val value: String) {
    LOW("low"),
    MODERATE("moderate"),
    HIGH("high");

    companion object {
        fun fromValue(value: String?): ExerciseIntensity? {
            return value?.let { v -> entries.find { it.value == v } }
        }
    }
}
