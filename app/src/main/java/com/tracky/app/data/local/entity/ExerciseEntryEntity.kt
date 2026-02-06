package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Exercise Entry Entity
 * 
 * Represents a logged exercise activity.
 * Calories calculated using MET formula: kcal = MET × weight_kg × hours
 */
@Entity(
    tableName = "exercise_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["createdAt"])
    ]
)
data class ExerciseEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Date of exercise (ISO date: "YYYY-MM-DD")
     */
    val date: String,
    
    /**
     * Time of exercise (ISO time: "HH:mm:ss")
     */
    val time: String,
    
    /**
     * Timestamp (epoch millis) for sorting
     */
    val timestamp: Long,
    
    /**
     * Activity name
     */
    val activityName: String,
    
    /**
     * Duration in minutes
     */
    val durationMinutes: Int,
    
    /**
     * MET value used for calculation
     */
    val metValue: Float,
    
    /**
     * User's weight at time of exercise (for calculation)
     */
    val userWeightKg: Float,
    
    /**
     * Calories burned (computed: MET × weight_kg × hours)
     */
    val caloriesBurned: Int,
    
    /**
     * Intensity level: "low", "moderate", "high"
     */
    val intensity: String?,
    
    /**
     * User's original input text
     */
    val originalInput: String?,
    
    /**
     * Provenance source: "met_compendium", "user_override"
     */
    val source: String,
    
    /**
     * Matched activity ID from MET compendium
     */
    val sourceId: String?,
    
    /**
     * Confidence score (0.0 - 1.0)
     */
    val confidence: Float,
    
    /**
     * Entry creation timestamp (epoch millis)
     */
    val createdAt: Long,
    
    /**
     * Entry last update timestamp (epoch millis)
     */
    val updatedAt: Long
)
