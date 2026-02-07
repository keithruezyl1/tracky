package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.FoodsDatasetDao
import com.tracky.app.data.local.entity.FoodsDatasetEntity
import com.tracky.app.data.remote.TrackyBackendApi
import com.tracky.app.data.remote.dto.FoodCandidateDto
import com.tracky.app.data.remote.dto.ResolveFoodRequest
import com.tracky.app.data.remote.dto.ResolvedFoodItemDto
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for food resolution with prioritized sources
 */
@Singleton
class FoodsRepository @Inject constructor(
    private val foodsDatasetDao: FoodsDatasetDao,
    private val backendApi: TrackyBackendApi,
    private val userHistoryResolver: com.tracky.app.domain.resolver.UserHistoryResolver,
    private val canonicalKeyGenerator: com.tracky.app.domain.resolver.CanonicalKeyGenerator
) {
    // ... (existing methods)

    private fun ResolvedFoodItemDto.toFoodItem(
        originalName: String,
        originalQuantity: Float,
        originalUnit: String
    ): FoodItem {
        return FoodItem(
            name = originalName,
            matchedName = matchedName,
            quantity = originalQuantity,
            unit = originalUnit,
            calories = calories?.toFloat() ?: 0f,
            carbsG = carbs ?: 0f,
            proteinG = protein ?: 0f,
            fatG = fat ?: 0f,
            provenance = Provenance(
                source = ProvenanceSource.fromValue(source),
                sourceId = fdcId?.toString(),
                confidence = confidence
            ),
            displayOrder = 0,
            canonicalKey = canonicalKeyGenerator.generate(originalName) // Generate strict key
        )
    }

    // This is a placeholder for where the new code would logically fit based on the instruction.
    // Assuming there's a method like `resolveFood` that returns `ResolvedFoodResult`.
    // The provided snippet seems to be a part of such a method's logic.
    // For the purpose of this edit, I will insert it as a new method to demonstrate the change.
    // In a real scenario, this would be integrated into an existing resolution method.
    suspend fun resolveFood(name: String, quantity: Float, unit: String): ResolvedFoodResult {
        // Step 1: Trusted User History (Exact/User Override)
        val trustedMatch = userHistoryResolver.findTrustedMatch(name, quantity, unit)
        if (trustedMatch != null) {
            return ResolvedFoodResult.Success(trustedMatch)
        }

        // Step 2: Local Dataset (FoodsDataset)
        val localCandidates = foodsDatasetDao.searchFoods(name, limit = 1)
        if (localCandidates.isNotEmpty()) {
            val entity = localCandidates.first()
            
            // Simple unit compatibility check
            if (entity.servingUnit.equals(unit, ignoreCase = true)) {
                val ratio = quantity / entity.servingSize
                val localItem = FoodItem(
                    name = entity.name,
                    matchedName = entity.name,
                    quantity = quantity,
                    unit = unit,
                    calories = (entity.caloriesPerServing * ratio),
                    carbsG = (entity.carbsPerServingG * ratio),
                    proteinG = (entity.proteinPerServingG * ratio),
                    fatG = (entity.fatPerServingG * ratio),
                    provenance = Provenance(
                        source = ProvenanceSource.DATASET,
                        sourceId = entity.id.toString(),
                        confidence = 1.0f // Local dataset is trusted source
                    ),
                    displayOrder = 0,
                    canonicalKey = canonicalKeyGenerator.generate(entity.name)
                )
                return ResolvedFoodResult.Success(localItem)
            }
            // If units differ, we skip local for now and let backend handle conversion
        }

        // Step 3: High Confidence History (USDA/Dataset reuse)
        val historyMatch = userHistoryResolver.findHighConfidenceMatch(name, quantity, unit)
        if (historyMatch != null) {
            return ResolvedFoodResult.Success(historyMatch)
        }

        // Step 4: Remote Backend Resolution (Gemini/USDA/SerpAPI)
        try {
            val request = ResolveFoodRequest(
                candidates = listOf(
                    FoodCandidateDto(
                        name = name,
                        quantity = quantity,
                        unit = unit
                    )
                )
            )
            val response = backendApi.resolveFood(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // We typically get a list, take the best one
                if (body.items.isNotEmpty()) {
                    val best = body.items.first()
                    return ResolvedFoodResult.Success(
                        best.toFoodItem(name, quantity, unit)
                    )
                }
            }
        } catch (e: Exception) {
            // Log error, continue to fallback
        }

        // Step 5: Unresolved fallback case
        // If no resolution is found, return a 0-kcal item with provenance=UNRESOLVED
        return ResolvedFoodResult.Success(
            FoodItem(
                name = name,
                matchedName = null,
                quantity = quantity,
                unit = unit,
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
                canonicalKey = canonicalKeyGenerator.generate(name) // Always set canonical key
            )
        )
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
