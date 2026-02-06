package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Saved Entry Entity
 * 
 * Templates for quick food/exercise logging.
 * User can save frequently logged items for reuse.
 */
@Entity(tableName = "saved_entries")
data class SavedEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * User-defined name for this saved entry
     */
    val name: String,
    
    /**
     * Type: "food" or "exercise"
     */
    val entryType: String,
    
    /**
     * JSON-serialized entry data
     * For food: array of items with name, quantity, unit, calories, macros
     * For exercise: activity name, duration, MET value
     */
    val entryDataJson: String,
    
    /**
     * Total calories (for quick display)
     */
    val totalCalories: Float,
    
    /**
     * Number of times this entry has been used
     */
    val useCount: Int = 0,
    
    /**
     * Last used timestamp (epoch millis)
     */
    val lastUsedAt: Long?,
    
    /**
     * Entry creation timestamp (epoch millis)
     */
    val createdAt: Long,
    
    /**
     * Entry last update timestamp (epoch millis)
     */
    val updatedAt: Long
)
