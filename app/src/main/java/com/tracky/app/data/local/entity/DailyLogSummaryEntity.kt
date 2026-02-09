package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "daily_log_summaries")
data class DailyLogSummaryEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val qualifyingEntriesCount: Int,
    val totalCaloriesConsumed: Float,
    val totalCaloriesBurned: Float,
    val metGoal: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)
