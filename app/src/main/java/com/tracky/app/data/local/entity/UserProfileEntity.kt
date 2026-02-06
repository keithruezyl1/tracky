package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Profile Entity
 * 
 * Stores user's physical profile data.
 * Single-user app so only one row expected.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Single user, fixed ID
    
    val heightCm: Float,
    val currentWeightKg: Float,
    val targetWeightKg: Float,
    
    /**
     * Unit preference: "metric" or "imperial"
     */
    val unitPreference: String,
    
    /**
     * Timezone ID (e.g., "America/New_York")
     */
    val timezone: String,
    
    /**
     * Computed BMI = weight(kg) / height(m)^2
     * Stored for quick access, recalculated when weight changes
     */
    val bmi: Float,
    
    /**
     * Timestamp when profile was created (epoch millis)
     */
    val createdAt: Long,
    
    /**
     * Timestamp when profile was last updated (epoch millis)
     */
    val updatedAt: Long
)
