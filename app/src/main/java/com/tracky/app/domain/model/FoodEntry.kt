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
    /**
     * Display order for this item
     */
    val displayOrder: Int,
    
    /**
     * Canonical key for exact reuse (e.g., "rice_white_cooked")
     */
    val canonicalKey: String? = null
) {
    /**
     * Check if item is valid for history reuse.
     * Rules:
     * 1. Macros must be valid (non-negative)
     * 2. Calories > 0 (unless explicit override)
     * 3. Source is not UNRESOLVED
     */
    fun isValidForReuse(minConfidence: Float = 0.8f): Boolean {
        // Rule 1: Source validity
        if (provenance.source == ProvenanceSource.UNRESOLVED) return false
        
        // Rule 2: User Override is always valid (user truth)
        if (provenance.source == ProvenanceSource.USER_OVERRIDE) return true
        
        // Rule 3: Quality check for auto-generated items
        if (calories <= 0f) return false
        if (carbsG < 0f || proteinG < 0f || fatG < 0f) return false
        
        // Rule 4: Confidence check
        return provenance.confidence >= minConfidence
    }
}

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
    USER_HISTORY("user_history"),
    INTERNET("internet"),
    USER_OVERRIDE("user_override"),
    UNRESOLVED("unresolved");

    companion object {
        fun fromValue(value: String): ProvenanceSource {
            return entries.find { it.value == value } ?: UNRESOLVED
        }
    }
}
