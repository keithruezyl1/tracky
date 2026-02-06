package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Daily Goal Entity
 * 
 * Stores daily calorie and macro goals.
 * Uses effectiveFromDate to support goal changes over time.
 */
@Entity(
    tableName = "daily_goals",
    indices = [
        Index(value = ["effectiveFromDate"], unique = true)
    ]
)
data class DailyGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Date from which this goal is effective (ISO date: "YYYY-MM-DD")
     */
    val effectiveFromDate: String,
    
    /**
     * Daily calorie goal in kcal
     */
    val calorieGoalKcal: Int,
    
    /**
     * Carbohydrate percentage (0-100)
     * carbs_pct + protein_pct + fat_pct must equal 100
     */
    val carbsPct: Int,
    
    /**
     * Protein percentage (0-100)
     */
    val proteinPct: Int,
    
    /**
     * Fat percentage (0-100)
     */
    val fatPct: Int,
    
    /**
     * Computed carbs target in grams
     * carbs_g = (calorieGoalKcal * carbsPct / 100) / 4
     */
    val carbsTargetG: Float,
    
    /**
     * Computed protein target in grams
     * protein_g = (calorieGoalKcal * proteinPct / 100) / 4
     */
    val proteinTargetG: Float,
    
    /**
     * Computed fat target in grams
     * fat_g = (calorieGoalKcal * fatPct / 100) / 9
     */
    val fatTargetG: Float,
    
    /**
     * Timestamp when this goal was created (epoch millis)
     */
    val createdAt: Long
) {
    init {
        require(carbsPct in 0..100) { "Carbs percentage must be 0-100" }
        require(proteinPct in 0..100) { "Protein percentage must be 0-100" }
        require(fatPct in 0..100) { "Fat percentage must be 0-100" }
        require(carbsPct + proteinPct + fatPct == 100) { "Macro percentages must sum to 100" }
    }
}
