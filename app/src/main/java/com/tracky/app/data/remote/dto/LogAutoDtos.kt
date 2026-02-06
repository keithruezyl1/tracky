package com.tracky.app.data.remote.dto

import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// Log Auto (Food/Exercise Auto-Detection) Request/Response
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class LogAutoRequest(
    val text: String? = null,
    val imageBase64: String? = null,
    val userWeightKg: Float
)

@Serializable
data class LogAutoResponse(
    val status: String,
    val entry_type: String, // "food", "exercise", "mixed", "none"
    val food_items: List<ParsedFoodItemDto> = emptyList(),
    val exercises: List<ParsedExerciseDto> = emptyList(),
    val narrative: String? = null,
    val requiresConfirmation: Boolean = true,
    val error: String? = null
)

@Serializable
data class ParsedExerciseDto(
    val activity: String,
    val durationMinutes: Int = 30,
    val intensity: String = "moderate",
    val confidence: Float = 0.5f,
    val suggestedQueries: List<String> = emptyList(),
    val metValue: Float? = null,  // MET value if extracted from image
    val caloriesBurned: Int? = null  // Calories if extracted from image
)
