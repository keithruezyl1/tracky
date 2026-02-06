package com.tracky.app.domain.resolver

import com.tracky.app.data.local.dao.FoodEntryDao
import com.tracky.app.data.local.entity.FoodItemEntity
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User History Resolver
 * 
 * Resolves food items by searching user's historical entries.
 * Handles quantity scaling to ensure accurate nutrition data.
 */
@Singleton
class UserHistoryResolver @Inject constructor(
    private val foodEntryDao: FoodEntryDao
) {
    /**
     * Resolve food item from user history
     * 
     * @param query Food name to search for
     * @param requestedQuantity Quantity requested by user
     * @param requestedUnit Unit requested by user
     * @return FoodItem if match found with scaled nutrition, null otherwise
     */
    suspend fun resolve(
        query: String,
        requestedQuantity: Float,
        requestedUnit: String
    ): FoodItem? {
        // 1. Lexical prefilter
        val candidates = foodEntryDao.searchUserHistory(query, limit = 50)
        if (candidates.isEmpty()) return null
        
        // 2. Exact match fast path (prioritize exact Name matches)
        val exactMatch = candidates.find { 
            it.name.equals(query, ignoreCase = true) ||
            it.matchedName?.equals(query, ignoreCase = true) == true
        }
        if (exactMatch != null) {
            return scaleToRequestedQuantity(exactMatch, requestedQuantity, requestedUnit)
        }
        
        // 3. Fallback: highest confidence lexical match
        return candidates.firstOrNull()?.let { 
            scaleToRequestedQuantity(it, requestedQuantity, requestedUnit)
        }
    }
    
    /**
     * Scale historical nutrition data to requested quantity
     * 
     * Example:
     *   Historical: 2 banana bread = 100 kcal
     *   Requested:  1 banana bread
     *   Result:     50 kcal (100 / 2 * 1)
     */
    private fun scaleToRequestedQuantity(
        historicalItem: FoodItemEntity,
        requestedQuantity: Float,
        requestedUnit: String
    ): FoodItem {
        // Avoid division by zero
        if (historicalItem.quantity == 0f) {
            return createUnscaledFoodItem(historicalItem, requestedQuantity, requestedUnit)
        }
        
        // Calculate per-unit nutrition values from historical entry
        val perUnitCalories = historicalItem.calories / historicalItem.quantity
        val perUnitProtein = historicalItem.proteinG / historicalItem.quantity
        val perUnitCarbs = historicalItem.carbsG / historicalItem.quantity
        val perUnitFat = historicalItem.fatG / historicalItem.quantity
        
        // TODO: Handle unit conversion (e.g., oz → g) if units differ
        // For Phase 1, assume units match or are compatible
        
        return FoodItem(
            id = 0, // New item, not yet persisted
            name = historicalItem.name,
            matchedName = historicalItem.matchedName,
            quantity = requestedQuantity,
            unit = requestedUnit,
            calories = perUnitCalories * requestedQuantity,
            proteinG = perUnitProtein * requestedQuantity,
            carbsG = perUnitCarbs * requestedQuantity,
            fatG = perUnitFat * requestedQuantity,
            provenance = Provenance(
                source = ProvenanceSource.USER_HISTORY,
                sourceId = historicalItem.id.toString(),
                confidence = historicalItem.confidence
            ),
            displayOrder = 0
        )
    }
    
    /**
     * Fallback for zero-quantity historical items (shouldn't happen, but defensive)
     */
    private fun createUnscaledFoodItem(
        historicalItem: FoodItemEntity,
        requestedQuantity: Float,
        requestedUnit: String
    ): FoodItem {
        return FoodItem(
            id = 0,
            name = historicalItem.name,
            matchedName = historicalItem.matchedName,
            quantity = requestedQuantity,
            unit = requestedUnit,
            calories = historicalItem.calories.toFloat(),
            proteinG = historicalItem.proteinG,
            carbsG = historicalItem.carbsG,
            fatG = historicalItem.fatG,
            provenance = Provenance(
                source = ProvenanceSource.USER_HISTORY,
                sourceId = historicalItem.id.toString(),
                confidence = historicalItem.confidence * 0.5f // Lower confidence for unscaled
            ),
            displayOrder = 0
        )
    }
}
