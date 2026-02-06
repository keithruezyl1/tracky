package com.tracky.app.domain.model

/**
 * Domain model for Exercise Entry
 */
data class ExerciseEntry(
    val id: Long = 0,
    val date: String,
    val time: String,
    val timestamp: Long,
    val items: List<ExerciseItem>, // Changed from single activity fields to list
    val totalCalories: Int,        // New field
    val totalDurationMinutes: Int, // New field
    val userWeightKg: Float,
    val originalInput: String?,
    val createdAt: Long,
    val updatedAt: Long
)
