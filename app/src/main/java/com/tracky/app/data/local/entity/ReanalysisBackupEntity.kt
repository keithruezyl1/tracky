package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to store backups of entries currently being re-analyzed.
 * Ensures that if the app is closed during re-analysis, the original data is not lost.
 */
@Entity(tableName = "reanalysis_backups")
data class ReanalysisBackupEntity(
    /**
     * PK is the original entry ID to ensure unique backups per entry.
     */
    @PrimaryKey
    val originalEntryId: Long,
    
    /**
     * Type of entry: "food" or "exercise"
     */
    val type: String,
    
    /**
     * Serialized domain model (FoodEntry or ExerciseEntry)
     */
    val dataJson: String,
    
    /**
     * Date of the entry (for cleanup purposes if needed)
     */
    val date: String,
    
    /**
     * Timestamp of backup creation
     */
    val createdAt: Long = System.currentTimeMillis()
)
