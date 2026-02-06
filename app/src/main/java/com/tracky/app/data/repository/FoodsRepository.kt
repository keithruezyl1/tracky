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
 * Repository for food dataset - implements dataset-first resolution
 */
@Singleton
class FoodsRepository @Inject constructor(
    private val foodsDatasetDao: FoodsDatasetDao,
    private val backendApi: TrackyBackendApi
) {
    /**
     * Search local dataset using FTS
     */
    suspend fun searchLocalDataset(query: String, limit: Int = 20): List<FoodsDatasetEntity> {
        // First try FTS search
        val ftsResults = foodsDatasetDao.searchFoods(query, limit)
        if (ftsResults.isNotEmpty()) {
            return ftsResults
        }

        // Fall back to synonym search
        val synonymResults = foodsDatasetDao.searchBySynonym(query, limit)
        if (synonymResults.isNotEmpty()) {
            return synonymResults
        }

        // Fall back to LIKE search
        return foodsDatasetDao.searchByName(query, limit)
    }

    /**
     * Resolve food item using dataset-first approach
     * 
     * Priority:
     * 1. Local dataset
     * 2. USDA FDC via backend proxy
     */
    suspend fun resolveFood(
        name: String,
        quantity: Float,
        unit: String
    ): ResolvedFoodResult {
        // Step 1: Try local dataset first
        val localMatch = searchLocalDataset(name, 1).firstOrNull()
        
        if (localMatch != null) {
            val multiplier = calculateMultiplier(quantity, unit, localMatch)
            return ResolvedFoodResult.Success(
                FoodItem(
                    name = name,
                    matchedName = localMatch.name,
                    quantity = quantity,
                    unit = unit,
                    calories = (localMatch.caloriesPerServing * multiplier).toInt(),
                    carbsG = localMatch.carbsPerServingG * multiplier,
                    proteinG = localMatch.proteinPerServingG * multiplier,
                    fatG = localMatch.fatPerServingG * multiplier,
                    provenance = Provenance(
                        source = ProvenanceSource.DATASET,
                        sourceId = localMatch.id.toString(),
                        confidence = 0.9f
                    ),
                    displayOrder = 0
                )
            )
        }

        // Step 2: Try USDA via backend
        try {
            val usdaResponse = backendApi.resolveFood(
                ResolveFoodRequest(
                    candidates = listOf(
                        FoodCandidateDto(name = name, quantity = quantity, unit = unit)
                    )
                )
            )

            if (usdaResponse.isSuccessful) {
                val body = usdaResponse.body()
                val resolvedItem = body?.items?.firstOrNull()
                
                if (resolvedItem != null && resolvedItem.resolved != false) {
                    return ResolvedFoodResult.Success(resolvedItem.toFoodItem(name, quantity, unit))
                }
            }
        } catch (e: Exception) {
            // Log error but continue to next fallback
        }

        // Step 3: Try Internet via backend
        try {
            val internetResponse = backendApi.resolveInternet(
                ResolveFoodRequest(
                    candidates = listOf(
                        FoodCandidateDto(name = name, quantity = quantity, unit = unit)
                    )
                )
            )

            if (internetResponse.isSuccessful) {
                val body = internetResponse.body()
                val resolvedItem = body?.items?.firstOrNull()
                
                if (resolvedItem != null && resolvedItem.resolved != false) {
                    return ResolvedFoodResult.Success(resolvedItem.toFoodItem(name, quantity, unit))
                }
            }
        } catch (e: Exception) {
            // Log error but continue
        }

        // Step 4: Return Unresolved (NOT User Override by default)
        return ResolvedFoodResult.Success(
            FoodItem(
                name = name,
                matchedName = null,
                quantity = quantity,
                unit = unit,
                calories = 0,
                carbsG = 0f,
                proteinG = 0f,
                fatG = 0f,
                provenance = Provenance(
                    source = ProvenanceSource.UNRESOLVED,
                    sourceId = null,
                    confidence = 0f
                ),
                displayOrder = 0
            )
        )
    }

    /**
     * Resolve multiple food items
     */
    suspend fun resolveFoods(
        items: List<Triple<String, Float, String>> // name, quantity, unit
    ): List<ResolvedFoodResult> {
        return items.map { (name, quantity, unit) ->
            resolveFood(name, quantity, unit)
        }
    }

    /**
     * Get food by local dataset ID
     */
    suspend fun getFoodById(id: Long): FoodsDatasetEntity? {
        return foodsDatasetDao.getFoodById(id)
    }

    /**
     * Get food by USDA FDC ID
     */
    suspend fun getFoodByFdcId(fdcId: Int): FoodsDatasetEntity? {
        return foodsDatasetDao.getFoodByFdcId(fdcId)
    }

    /**
     * Calculate serving multiplier based on quantity and unit
     */
    private fun calculateMultiplier(
        quantity: Float,
        unit: String,
        food: FoodsDatasetEntity
    ): Float {
        // Simplified multiplier calculation
        // In real implementation, would handle unit conversions
        return when {
            unit.equals(food.servingUnit, ignoreCase = true) -> 
                quantity / food.servingSize
            unit in listOf("g", "gram", "grams") && food.servingUnit == "g" -> 
                quantity / food.servingSize
            else -> quantity // Default: treat as serving count
        }
    }

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
            calories = calories ?: 0,
            carbsG = carbs ?: 0f,
            proteinG = protein ?: 0f,
            fatG = fat ?: 0f,
            provenance = Provenance(
                source = ProvenanceSource.fromValue(source),
                sourceId = fdcId?.toString(),
                confidence = confidence
            ),
            displayOrder = 0
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
