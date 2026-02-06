package com.tracky.app.data.remote.dto

import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// Log Food Request/Response
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class LogFoodRequest(
    val text: String? = null,
    val imageBase64: String? = null,
    val userWeightKg: Float
)

@Serializable
data class LogFoodResponse(
    val status: String,
    val items: List<ParsedFoodItemDto>,
    val narrative: String?,
    val requiresConfirmation: Boolean,
    val error: String? = null
)

@Serializable
data class ParsedFoodItemDto(
    val name: String,
    val quantity: Float = 1f,
    val unit: String = "serving",
    val confidence: Float = 0.5f,
    val suggestedQueries: List<String> = emptyList()
)

// ─────────────────────────────────────────────────────────────────────────────
// Resolve Food Request/Response
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class ResolveFoodRequest(
    val candidates: List<FoodCandidateDto>
)

@Serializable
data class FoodCandidateDto(
    val name: String,
    val quantity: Float? = null,
    val unit: String? = null,
    val fdcId: Int? = null
)

@Serializable
data class ResolveFoodResponse(
    val items: List<ResolvedFoodItemDto>,
    val totals: MacroTotalsDto,
    val allResolved: Boolean,
    val error: String? = null
)

@Serializable
data class ResolvedFoodItemDto(
    val name: String,
    val matchedName: String? = null,
    val fdcId: Int? = null,
    val quantity: Float,
    val unit: String,
    val calories: Int? = null,
    val protein: Float? = null,
    val carbs: Float? = null,
    val fat: Float? = null,
    val source: String,
    val confidence: Float,
    val resolved: Boolean? = null,
    val requiresManualEntry: Boolean? = null
)

@Serializable
data class MacroTotalsDto(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
