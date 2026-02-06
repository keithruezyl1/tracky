package com.tracky.app.domain.model

/**
 * Domain model for Daily Goal
 */
data class DailyGoal(
    val id: Long = 0,
    val effectiveFromDate: String,
    val calorieGoalKcal: Int,
    val carbsPct: Int,
    val proteinPct: Int,
    val fatPct: Int,
    val carbsTargetG: Float,
    val proteinTargetG: Float,
    val fatTargetG: Float,
    val createdAt: Long
) {
    init {
        require(carbsPct + proteinPct + fatPct == 100) {
            "Macro percentages must sum to 100"
        }
    }

    companion object {
        /**
         * Calculate macro targets in grams from percentages
         * 
         * Carbs/protein: 4 kcal/g
         * Fat: 9 kcal/g
         */
        fun calculateMacroTargets(
            calorieGoal: Int,
            carbsPct: Int,
            proteinPct: Int,
            fatPct: Int
        ): MacroTargets {
            val carbsG = (calorieGoal * carbsPct / 100f) / 4f
            val proteinG = (calorieGoal * proteinPct / 100f) / 4f
            val fatG = (calorieGoal * fatPct / 100f) / 9f
            return MacroTargets(carbsG, proteinG, fatG)
        }
    }
}

/**
 * Macro targets in grams
 */
data class MacroTargets(
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float
)

/**
 * Macro distribution percentages
 */
data class MacroDistribution(
    val carbsPct: Int,
    val proteinPct: Int,
    val fatPct: Int
) {
    val isValid: Boolean
        get() = carbsPct in 0..100 &&
                proteinPct in 0..100 &&
                fatPct in 0..100 &&
                carbsPct + proteinPct + fatPct == 100
    
    val total: Int
        get() = carbsPct + proteinPct + fatPct
}
