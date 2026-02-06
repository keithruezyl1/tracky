package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for Food Entry
 */
data class FoodEntry(
    val id: Long = 0,
    val date: String,
    val time: String,
    val timestamp: Long,
    val totalCalories: Float,
    val totalCarbsG: Float,
    val totalProteinG: Float,
    val totalFatG: Float,
    val analysisNarrative: String?,
    val photoPath: String?,
    val originalInput: String?,
    val items: List<FoodItem>,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Domain model for Food Item
 */
data class FoodItem(
    val id: Long = 0,
    val name: String,
    val matchedName: String?,
    val quantity: Float,
    val unit: String,
    val calories: Float,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float,
    val provenance: Provenance,
    val displayOrder: Int
)

/**
 * Provenance tracking for nutrition data
 */
@Serializable
data class Provenance(
    val source: ProvenanceSource,
    val sourceId: String?,
    val confidence: Float
)

/**
 * Source of nutrition data
 */
@Serializable
enum class ProvenanceSource(val value: String) {
    DATASET("dataset"),
    USDA_FDC("usda_fdc"),
    INTERNET("internet"),
    USER_OVERRIDE("user_override"),
    UNRESOLVED("unresolved");

    companion object {
        fun fromValue(value: String): ProvenanceSource {
            return entries.find { it.value == value } ?: UNRESOLVED
        }
    }
}
