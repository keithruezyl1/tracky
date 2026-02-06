package com.tracky.app.domain.model

/**
 * Domain model for Saved Entry templates
 */
data class SavedEntry(
    val id: Long = 0,
    val name: String,
    val entryType: SavedEntryType,
    val data: SavedEntryData,
    val totalCalories: Float,
    val useCount: Int,
    val lastUsedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Saved entry type
 */
enum class SavedEntryType(val value: String) {
    FOOD("food"),
    EXERCISE("exercise");

    companion object {
        fun fromValue(value: String): SavedEntryType {
            return entries.find { it.value == value } ?: FOOD
        }
    }
}

/**
 * Saved entry data
 */
sealed class SavedEntryData {
    data class FoodData(
        val items: List<SavedFoodItem>
    ) : SavedEntryData()

    data class ExerciseData(
        val items: List<SavedExerciseItem>
    ) : SavedEntryData()
}

/**
 * Saved food item for templates
 */
data class SavedFoodItem(
    val name: String,
    val quantity: Float,
    val unit: String,
    val calories: Float,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float
)

/**
 * Saved exercise item for templates
 */
data class SavedExerciseItem(
    val activityName: String,
    val durationMinutes: Int,
    val metValue: Float
)
