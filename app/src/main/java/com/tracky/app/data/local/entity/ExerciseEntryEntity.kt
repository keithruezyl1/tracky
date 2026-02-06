package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_entries")
data class ExerciseEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val time: String,
    val timestamp: Long,
    val totalCalories: Int,
    val totalDurationMinutes: Int,
    val userWeightKg: Float,
    val originalInput: String?,
    val createdAt: Long,
    val updatedAt: Long
)
