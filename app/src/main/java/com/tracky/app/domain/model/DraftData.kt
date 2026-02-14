package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

import kotlinx.datetime.LocalDate

sealed class DraftData {
    data class FoodDraft(
        val items: List<DraftFoodItem>,
        val totalCalories: Float,
        val totalCarbsG: Float,
        val totalProteinG: Float,
        val totalFatG: Float,
        val narrative: String?,
        val date: LocalDate
    ) : DraftData()

    data class ExerciseDraft(
        val items: List<DraftExerciseItem>,
        val totalCalories: Float,
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
    val calories: Float,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float,
    val provenance: Provenance,
    val resolved: Boolean,
    val isManualMacros: Boolean = false,
    val isAnalyzing: Boolean = false,
    val analysisRevision: Long = 0
)

@Serializable
data class DraftExerciseItem(
    val activity: String,
    val durationMinutes: Int,
    val metValue: Float,
    val caloriesBurned: Float,
    val intensity: ExerciseIntensity,
    val resolved: Boolean,
    val isManual: Boolean = false,
    val isAnalyzing: Boolean = false,
    val analysisRevision: Long = 0
)
