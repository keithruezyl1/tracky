package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

import kotlinx.datetime.LocalDate

sealed class DraftData {
    data class FoodDraft(
        val items: List<DraftFoodItem>,
        val totalCalories: Int,
        val totalCarbsG: Float,
        val totalProteinG: Float,
        val totalFatG: Float,
        val narrative: String?,
        val date: LocalDate
    ) : DraftData()

    data class ExerciseDraft(
        val items: List<DraftExerciseItem>,
        val totalCalories: Int,
        val totalDurationMinutes: Int,
        val date: LocalDate
    ) : DraftData()
}

@Serializable
data class DraftFoodItem(
    val name: String,
    val matchedName: String?,
    val quantity: Double,
    val unit: String,
    val calories: Int,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float,
    val provenance: Provenance,
    val resolved: Boolean
)
