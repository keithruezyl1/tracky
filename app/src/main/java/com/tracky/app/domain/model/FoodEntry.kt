package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for Food Entry
 */
@Serializable
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
@Serializable
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
    val canonicalKey: String? = null,
    
    /**
     * Whether macros were manually edited by the user.
     * If true, AI auto-reanalysis will NOT overwrite these values.
     */
    val isManualMacros: Boolean = false,

    /**
     * Monotonic revision number for analysis.
     * Used to prevent race conditions during async updates.
     */
    val analysisRevision: Long = 0,

    /**
     * Pending suggested update from AI (if name changed but macros were manual).
     * User can choose to apply or dismiss this.
     */
    val pendingSuggestion: FoodItem? = null,

    /**
     * Transient state for UI loading indicator.
     * Not serialized.
     */
    @kotlinx.serialization.Transient
    val isAnalyzing: Boolean = false
) {
    /**
     * Check if item is eligible for anchor reuse (skip AI call).
     * Rules:
     * - USER_OVERRIDE (Manual) → always reuse
     * - DATASET → always reuse
     * - AI_ESTIMATE/USER_HISTORY (Saved) → reuse only if reusedCount >= threshold
     * - UNRESOLVED → never reuse
     */
    fun isEligibleForAnchorReuse(reuseCountThreshold: Int = 3): Boolean {
        if (provenance.source == ProvenanceSource.UNRESOLVED) return false
        if (calories <= 0f) return false
        if (carbsG < 0f || proteinG < 0f || fatG < 0f) return false

        return when (provenance.source) {
            ProvenanceSource.USER_OVERRIDE -> true
            ProvenanceSource.DATASET -> true
            ProvenanceSource.AI_ESTIMATE,
            ProvenanceSource.USER_HISTORY -> provenance.reusedCount >= reuseCountThreshold
            else -> false
        }
    }

    /**
     * Legacy compat: check if item is valid for general reuse.
     */
    fun isValidForReuse(minConfidence: Float = 0.8f): Boolean {
        if (provenance.source == ProvenanceSource.UNRESOLVED) return false
        if (provenance.source == ProvenanceSource.USER_OVERRIDE) return true
        if (calories <= 0f) return false
        if (carbsG < 0f || proteinG < 0f || fatG < 0f) return false
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
    val confidence: Float,
    /**
     * Number of times this item has been reused from history.
     * Higher count = stronger anchor.
     */
    val reusedCount: Int = 0
)

/**
 * Source of nutrition data
 */
@Serializable
enum class ProvenanceSource(val value: String) {
    DATASET("dataset"),
    @Deprecated("USDA resolution removed — use AI_ESTIMATE instead")
    USDA_FDC("usda_fdc"),
    USER_HISTORY("user_history"),
    @Deprecated("Internet resolution removed — use AI_ESTIMATE instead")
    INTERNET("internet"),
    USER_OVERRIDE("user_override"),
    AI_ESTIMATE("ai_estimate"),
    UNRESOLVED("unresolved");

    companion object {
        fun fromValue(value: String): ProvenanceSource {
            return entries.find { it.value == value } ?: UNRESOLVED
        }
    }
}

/**
 * Reconciliation authority modes.
 * AUTO: Calories are truth → scale macros to match.
 * MANUAL: User-entered macros are truth → only flag, never auto-change.
 */
enum class ReconciliationMode { AUTO, MANUAL }

/**
 * Utility to enforce kcal ↔ macro consistency using the 4/4/9 rule.
 */
object MacroReconciler {
    private const val TOLERANCE = 0.05f // 5%

    /**
     * Compute kcal from macros: Carbs*4 + Protein*4 + Fat*9
     */
    fun computeCaloriesFromMacros(carbsG: Float, proteinG: Float, fatG: Float): Float {
        return (carbsG * 4f) + (proteinG * 4f) + (fatG * 9f)
    }

    /**
     * Check if macros are within tolerance of declared calories.
     */
    fun isMismatch(calories: Float, carbsG: Float, proteinG: Float, fatG: Float): Boolean {
        if (calories <= 0f) return false
        val computed = computeCaloriesFromMacros(carbsG, proteinG, fatG)
        val delta = kotlin.math.abs(computed - calories) / calories
        return delta > TOLERANCE
    }

    /**
     * Reconcile macros to match declared calories, preserving their ratio.
     * Only call this in AUTO mode (AI_ESTIMATE / SAVED items).
     */
    fun reconcile(calories: Float, carbsG: Float, proteinG: Float, fatG: Float): Triple<Float, Float, Float> {
        val computed = computeCaloriesFromMacros(carbsG, proteinG, fatG)
        if (computed <= 0f) return Triple(carbsG, proteinG, fatG)
        val scale = calories / computed
        return Triple(carbsG * scale, proteinG * scale, fatG * scale)
    }
}
