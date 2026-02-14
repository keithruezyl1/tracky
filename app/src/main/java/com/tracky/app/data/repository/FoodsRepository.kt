package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.FoodsDatasetDao
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.MacroReconciler
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.domain.resolver.CanonicalKeyGenerator
import com.tracky.app.domain.resolver.FoodResolutionConfig
import com.tracky.app.domain.resolver.UserHistoryResolver
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for food resolution — AI-first model.
 *
 * Resolution priority:
 * 1. USER_OVERRIDE (Manual) anchors — always skip AI
 * 2. Local Dataset (curated) — always skip AI
 * 3. High-reuse Saved items (AI_ESTIMATE/USER_HISTORY with reusedCount >= threshold) — skip AI
 * 4. AI Estimation fallback — use structured output from GPT-4o mini (via log/auto endpoint)
 *
 * USDA and Internet resolution have been removed.
 */
@Singleton
class FoodsRepository @Inject constructor(
    private val foodsDatasetDao: FoodsDatasetDao,
    private val userHistoryResolver: UserHistoryResolver,
    private val canonicalKeyGenerator: CanonicalKeyGenerator
) {

    /**
     * Resolve a food item to nutrition data using the eligibility-gated flywheel.
     *
     * @param name Food name from AI parsing step
     * @param quantity Parsed quantity
     * @param unit Parsed unit
     * @param aiCalories Optional: AI-estimated calories (from log/auto response)
     * @param aiCarbsG Optional: AI-estimated carbs
     * @param aiProteinG Optional: AI-estimated protein
     * @param aiFatG Optional: AI-estimated fat
     */
    suspend fun resolveFood(
        name: String,
        quantity: Float,
        unit: String,
        aiCalories: Float? = null,
        aiCarbsG: Float? = null,
        aiProteinG: Float? = null,
        aiFatG: Float? = null
    ): ResolvedFoodResult {
        // ─── Step 0: Normalize Inputs ───
        val normalizedUnit = normalizeUnit(unit)
        val canonicalKey = canonicalKeyGenerator.generate(name)

        // ─── Step 1: Trusted User History (USER_OVERRIDE / Manual) ───
        val trustedMatch = userHistoryResolver.findTrustedMatch(name, quantity, normalizedUnit)
        if (trustedMatch != null && trustedMatch.isEligibleForAnchorReuse()) {
            return ResolvedFoodResult.Success(
                trustedMatch.copy(
                    provenance = trustedMatch.provenance.copy(
                        reusedCount = trustedMatch.provenance.reusedCount + 1
                    )
                )
            )
        }

        // ─── Step 2: Local Dataset (curated) ───
        val localCandidates = foodsDatasetDao.searchFoods(name, limit = 1)
        if (localCandidates.isNotEmpty()) {
            val entity = localCandidates.first()
            if (entity.servingUnit.equals(unit, ignoreCase = true)) {
                val ratio = quantity / entity.servingSize
                val localItem = FoodItem(
                    name = entity.name,
                    matchedName = entity.name,
                    quantity = quantity,
                    unit = normalizedUnit,
                    calories = (entity.caloriesPerServing * ratio),
                    carbsG = (entity.carbsPerServingG * ratio),
                    proteinG = (entity.proteinPerServingG * ratio),
                    fatG = (entity.fatPerServingG * ratio),
                    provenance = Provenance(
                        source = ProvenanceSource.DATASET,
                        sourceId = entity.id.toString(),
                        confidence = 1.0f
                    ),
                    displayOrder = 0,
                    canonicalKey = canonicalKey
                )
                return ResolvedFoodResult.Success(localItem)
            }
        }

        // ─── Step 3: High-Reuse Saved History (AI_ESTIMATE/USER_HISTORY with enough reusedCount) ───
        val historyMatch = userHistoryResolver.findHighConfidenceMatch(name, quantity, normalizedUnit)
        if (historyMatch != null && historyMatch.isEligibleForAnchorReuse(FoodResolutionConfig.REUSE_COUNT_THRESHOLD)) {
            // Check if AI data is available and conflicts with stored values
            val pendingSuggestion = if (aiCalories != null && aiCalories > 0f) {
                val conflictRatio = kotlin.math.abs(aiCalories - historyMatch.calories) / historyMatch.calories
                if (conflictRatio > FoodResolutionConfig.SUGGESTION_CONFLICT_THRESHOLD) {
                    // AI disagrees — store as pending suggestion, do NOT overwrite
                    buildAiItem(name, quantity, normalizedUnit, aiCalories, aiCarbsG, aiProteinG, aiFatG, canonicalKey)
                } else null
            } else null

            return ResolvedFoodResult.Success(
                historyMatch.copy(
                    provenance = historyMatch.provenance.copy(
                        reusedCount = historyMatch.provenance.reusedCount + 1
                    ),
                    pendingSuggestion = pendingSuggestion
                )
            )
        }

        // ─── Step 4: AI Estimation (GPT-4o mini output) ───
        if (aiCalories != null && aiCalories > 0f) {
            val aiItem = buildAiItem(name, quantity, normalizedUnit, aiCalories, aiCarbsG, aiProteinG, aiFatG, canonicalKey)
            return ResolvedFoodResult.Success(aiItem)
        }

        // ─── Step 5: Low-reuse history (not yet eligible for anchor, but better than unresolved) ───
        if (historyMatch != null) {
            return ResolvedFoodResult.Success(
                historyMatch.copy(
                    provenance = historyMatch.provenance.copy(
                        reusedCount = historyMatch.provenance.reusedCount + 1
                    )
                )
            )
        }

        // ─── Step 6: Unresolved fallback ───
        return ResolvedFoodResult.Success(
            FoodItem(
                name = name,
                matchedName = null,
                quantity = quantity,
                unit = normalizedUnit,
                calories = 0f,
                carbsG = 0f,
                proteinG = 0f,
                fatG = 0f,
                provenance = Provenance(
                    source = ProvenanceSource.UNRESOLVED,
                    sourceId = null,
                    confidence = 0f
                ),
                displayOrder = 0,
                canonicalKey = canonicalKey
            )
        )
    }

    /**
     * Build a FoodItem from AI estimation, with macro reconciliation applied.
     */
    private fun buildAiItem(
        name: String,
        quantity: Float,
        unit: String,
        aiCalories: Float,
        aiCarbsG: Float?,
        aiProteinG: Float?,
        aiFatG: Float?,
        canonicalKey: String
    ): FoodItem {
        var carbs = aiCarbsG ?: 0f
        var protein = aiProteinG ?: 0f
        var fat = aiFatG ?: 0f

        // AUTO reconciliation: scale macros to match declared calories
        if (MacroReconciler.isMismatch(aiCalories, carbs, protein, fat)) {
            val (rc, rp, rf) = MacroReconciler.reconcile(aiCalories, carbs, protein, fat)
            carbs = rc
            protein = rp
            fat = rf
        }

        return FoodItem(
            name = name,
            matchedName = null,
            quantity = quantity,
            unit = unit,
            calories = aiCalories,
            carbsG = carbs,
            proteinG = protein,
            fatG = fat,
            provenance = Provenance(
                source = ProvenanceSource.AI_ESTIMATE,
                sourceId = null,
                confidence = 0.7f, // AI baseline confidence
                reusedCount = 0
            ),
            displayOrder = 0,
            canonicalKey = canonicalKey
        )
    }

    /**
     * Normalize unit strings to the allowed set.
     */
    private fun normalizeUnit(unit: String): String {
        val lower = unit.lowercase().trim().removeSuffix(".")
        return when (lower) {
            "tablespoon", "tbsp", "tbsp." -> "tbsp"
            "teaspoon", "tsp", "tsp." -> "tsp"
            "gram", "grams", "g", "g." -> "g"
            "ounce", "ounces", "oz", "oz." -> "oz"
            "milliliter", "milliliters", "ml", "ml." -> "ml"
            "whole", "item", "fruit", "piece", "pieces" -> "piece"
            "cup", "cups" -> "cup"
            "bottle", "bottles" -> "bottle"
            "can", "cans" -> "can"
            "package", "packages", "pkg", "packet" -> "package"
            "slice", "slices" -> "slice"
            "bowl", "bowls" -> "bowl"
            "glass", "glasses" -> "glass"
            "serving", "servings" -> "serving"
            else -> "serving"
        }
    }
}


/**
 * Result type for food resolution
 */
sealed class ResolvedFoodResult {
    data class Success(val foodItem: FoodItem) : ResolvedFoodResult()
    data object NotFound : ResolvedFoodResult()
    data class Error(val message: String) : ResolvedFoodResult()
}
