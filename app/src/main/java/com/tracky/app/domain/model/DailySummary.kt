package com.tracky.app.domain.model

/**
 * Daily summary combining all data for the dashboard
 */
data class DailySummary(
    val date: String,
    val goal: DailyGoal?,
    val foodCalories: Int,
    val exerciseCalories: Int,
    val carbsConsumedG: Float,
    val proteinConsumedG: Float,
    val fatConsumedG: Float,
    val foodEntries: List<FoodEntry>,
    val exerciseEntries: List<ExerciseEntry>
) {
    /**
     * Remaining calories formula per PRD:
     * remaining = calorie_goal - food_kcal + exercise_kcal
     */
    val remainingCalories: Int
        get() = (goal?.calorieGoalKcal ?: 0) - foodCalories + exerciseCalories

    /**
     * Progress toward calorie goal (0.0 to 1.0+)
     */
    val calorieProgress: Float
        get() = if (goal != null && goal.calorieGoalKcal > 0) {
            foodCalories.toFloat() / goal.calorieGoalKcal
        } else 0f

    /**
     * Check if over calorie goal
     */
    val isOverGoal: Boolean
        get() = remainingCalories < 0
}
