package com.tracky.app.domain.resolver

import com.tracky.app.data.local.dao.FoodEntryDao
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.data.mapper.toDomain
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

/**
 * User History Resolver
 * 
 * Resolves food items by searching user's historical entries.
 * Handles quantity scaling to ensure accurate nutrition data.
 * Uses strict hybrid matching and canonical keys for high-quality reuse.
 */
@Singleton
class UserHistoryResolver @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val canonicalKeyGenerator: CanonicalKeyGenerator
) {

    /**
     * Find a trusted match from user history (Source = USER_OVERRIDE).
     * Uses Hybrid Matcher (Canonical -> Exact -> Similarity).
     */
    suspend fun findTrustedMatch(query: String, requestedQuantity: Float, requestedUnit: String): FoodItem? {
        val canonicalQuery = canonicalKeyGenerator.generate(query)
        val normalizedQuery = normalizeText(query)
        
        // 1. Get candidates (broad search)
        val candidates = foodEntryDao.searchUserHistory(query, limit = 50)
            .filter { it.source == ProvenanceSource.USER_OVERRIDE.value }
            .map { it.toDomain() }
            .filter { it.isValidForReuse(minConfidence = 0.0f) } // User override is trusted regardless of confidence

        // 2. Canonical Match (Best)
        val canonicalMatch = candidates.find { 
            it.canonicalKey == canonicalQuery 
        }
        if (canonicalMatch != null) return scaleToRequestedQuantity(canonicalMatch, requestedQuantity, requestedUnit)

        // 3. Hybrid Similarity Match
        val bestHybrid = candidates
            .map { item -> item to computeMatchScore(normalizedQuery, canonicalQuery, item) }
            .filter { it.second >= FoodResolutionConfig.HISTORY_MIN_REUSE_CONFIDENCE }
            .maxByOrNull { it.second }
            ?.first

        return bestHybrid?.let { scaleToRequestedQuantity(it, requestedQuantity, requestedUnit) }
    }

    /**
     * Find a high-confidence match from user history (Source = USDA/Dataset).
     * Reuses past successful resolutions.
     */
    suspend fun findHighConfidenceMatch(query: String, requestedQuantity: Float, requestedUnit: String): FoodItem? {
        val canonicalQuery = canonicalKeyGenerator.generate(query)
        val normalizedQuery = normalizeText(query)

        val candidates = foodEntryDao.searchUserHistory(query, limit = 50)
            .filter { 
                it.source == ProvenanceSource.USDA_FDC.value || 
                it.source == ProvenanceSource.DATASET.value 
            }
            .map { it.toDomain() }
            .filter { it.isValidForReuse(minConfidence = FoodResolutionConfig.USDA_MIN_CONFIDENCE) }

        // Hybrid Matcher for USDA items
        val bestMatch = candidates
            .map { item -> item to computeMatchScore(normalizedQuery, canonicalQuery, item) }
            .filter { it.second >= FoodResolutionConfig.HISTORY_MIN_REUSE_CONFIDENCE } // Match quality must still be high
            .maxByOrNull { it.second }
            ?.first

        return bestMatch?.let { scaleToRequestedQuantity(it, requestedQuantity, requestedUnit) }
    }

    /**
     * Fallback resolution using fuzzy matching (legacy).
     */
    suspend fun resolve(query: String, requestedQuantity: Float, requestedUnit: String): FoodItem? {
        // Try exact/trusted first just in case
        val trusted = findTrustedMatch(query, requestedQuantity, requestedUnit)
        if (trusted != null) return trusted

        val candidates = foodEntryDao.searchUserHistory(query, limit = 50)
            .map { it.toDomain() }
            .filter { it.isValidForReuse(minConfidence = FoodResolutionConfig.HISTORY_MIN_REUSE_CONFIDENCE) }
            
        return candidates.firstOrNull()?.let { 
            scaleToRequestedQuantity(it, requestedQuantity, requestedUnit)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Hybrid Matching Logic
    // ─────────────────────────────────────────────────────────────────────────────

    private fun normalizeText(text: String): String {
        var processed = text.trim().lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "") // Remove punctuation
            .replace(Regex("\\s+"), " ") // Collapse spaces
        
        // Expand synonyms (Shared Config)
        FoodResolutionConfig.SYNONYM_MAP.forEach { (term, expansion) ->
            if (processed.contains(term)) {
                processed = processed.replace(term, expansion)
            }
        }
        
        return processed
    }

    private fun computeMatchScore(queryNorm: String, queryCanonical: String, item: FoodItem): Float {
        val itemNorm = normalizeText(item.name)
        val matchedNorm = item.matchedName?.let { normalizeText(it) } ?: ""
        val itemCanonical = item.canonicalKey ?: ""

        // Direct hit shortcuts
        if (queryCanonical.isNotEmpty() && queryCanonical == itemCanonical) return 1.0f
        if (queryNorm == itemNorm || queryNorm == matchedNorm) return 1.0f

        // Compute scores against Name and MatchedName
        val scoreName = hybridScore(queryNorm, itemNorm)
        val scoreMatched = hybridScore(queryNorm, matchedNorm)
        
        return max(scoreName, scoreMatched)
    }

    private fun hybridScore(s1: String, s2: String): Float {
        if (s1.isEmpty() || s2.isEmpty()) return 0f
        
        val jaccard = jaccardSimilarity(s1, s2)
        val lev = normalizedLevenshtein(s1, s2)
        val editScore = 1.0f - lev // Invert distance to get similarity
        
        // Weighted Score from Config
        return (FoodResolutionConfig.SCORE_WEIGHT_JACCARD * jaccard) + 
               (FoodResolutionConfig.SCORE_WEIGHT_LEVENSHTEIN * editScore)
    }

    private fun jaccardSimilarity(s1: String, s2: String): Float {
        val tokens1 = s1.split(" ").toSet()
        val tokens2 = s2.split(" ").toSet()
        val intersection = tokens1.intersect(tokens2).size
        val union = tokens1.union(tokens2).size
        return if (union == 0) 0f else intersection.toFloat() / union
    }

    private fun normalizedLevenshtein(s1: String, s2: String): Float {
        val maxLen = max(s1.length, s2.length)
        if (maxLen == 0) return 0f
        val dist = levenshtein(s1, s2)
        return dist.toFloat() / maxLen
    }

    private fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLen = lhs.length
        val rhsLen = rhs.length

        var cost = IntArray(lhsLen + 1) { it }
        var newCost = IntArray(lhsLen + 1) { 0 }

        for (i in 1..rhsLen) {
            newCost[0] = i
            for (j in 1..lhsLen) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLen]
    }

    /**
     * Scale historical nutrition data to requested quantity
     */
    private fun scaleToRequestedQuantity(
        item: FoodItem,
        requestedQuantity: Float,
        requestedUnit: String
    ): FoodItem {
        // Avoid division by zero
        if (item.quantity == 0f) {
            return item.copy(
                 quantity = requestedQuantity,
                 unit = requestedUnit,
                 provenance = item.provenance.copy(confidence = item.provenance.confidence * 0.5f)
            )
        }
        
        // Calculate per-unit nutrition values from historical entry
        val perUnitCalories = item.calories / item.quantity
        val perUnitProtein = item.proteinG / item.quantity
        val perUnitCarbs = item.carbsG / item.quantity
        val perUnitFat = item.fatG / item.quantity
        
        // TODO: Handle unit conversion (e.g., oz → g) if units differ
        // For Phase 1, assume units match or are compatible
        
        return item.copy(
            quantity = requestedQuantity,
            unit = requestedUnit,
            calories = perUnitCalories * requestedQuantity,
            proteinG = perUnitProtein * requestedQuantity,
            carbsG = perUnitCarbs * requestedQuantity,
            fatG = perUnitFat * requestedQuantity
        )
    }
}
