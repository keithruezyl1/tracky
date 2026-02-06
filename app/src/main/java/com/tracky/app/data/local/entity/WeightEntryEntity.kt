package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Weight Entry Entity
 * 
 * Tracks weight measurements over time.
 */
@Entity(
    tableName = "weight_entries",
    indices = [
        Index(value = ["date"], unique = false),
        Index(value = ["timestamp"])
    ]
)
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Date of weight measurement (ISO date: "YYYY-MM-DD")
     * Only one entry per date allowed.
     */
    val date: String,
    
    /**
     * Weight in kilograms
     */
    val weightKg: Float,
    
    /**
     * Optional note
     */
    val note: String?,
    
    /**
     * Timestamp (epoch millis) for precise sorting
     */
    val timestamp: Long,
    
    /**
     * Entry creation timestamp (epoch millis)
     */
    val createdAt: Long,
    
    /**
     * Entry last update timestamp (epoch millis)
     */
    val updatedAt: Long
)
