package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_items",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["entryId"])]
)
data class ExerciseItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: Long,
    val activityName: String,
    val durationMinutes: Int,
    val metValue: Float,
    val caloriesBurned: Float,
    val intensity: String?,    // Store as string from enum
    val source: String,        // Provenance source
    val confidence: Float,     // Provenance confidence
    val displayOrder: Int
)
