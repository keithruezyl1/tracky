package com.tracky.app.domain.resolver

/**
 * Configuration constants for food resolution logic.
 *
 * AI-first model: USDA/Internet resolution deprecated.
 * Trust is determined by provenance level + reusedCount.
 */
object FoodResolutionConfig {
    // Minimum reuse count before a Saved item can skip AI re-estimation
    const val REUSE_COUNT_THRESHOLD = 3

    // If new AI output differs from stored Saved item by more than this, flag as conflict
    const val SUGGESTION_CONFLICT_THRESHOLD = 0.10f

    // Legacy: History reuse similarity threshold
    const val HISTORY_MIN_REUSE_CONFIDENCE = 0.8f

    // Hybrid matching weights
    const val SCORE_WEIGHT_JACCARD = 0.5f
    const val SCORE_WEIGHT_LEVENSHTEIN = 0.5f

    // Shared Synonym Map (Lower case keys) — Filipino & common brand terms
    val SYNONYM_MAP = mapOf(
        "sinangag" to "garlic fried rice",
        "taho" to "silken tofu arnibal",
        "champorado" to "chocolate rice porridge",
        "chickenjoy" to "fried chicken",
        "pandesal" to "bread roll",
        "itlog" to "egg",
        "kanin" to "rice",
        "sinaing" to "steamed rice",
        "longganisa" to "pork sausage",
        "tocino" to "cured pork",
        "adobo" to "chicken pork adobo",
        "sinigang" to "tamarind soup",
        "tinola" to "chicken ginger soup",
        "lumpia" to "spring roll",
        "bibingka" to "rice cake",
        "leche flan" to "caramel custard",
        "halo-halo" to "shaved ice dessert",
        "halohalo" to "shaved ice dessert",
        "tapsilog" to "beef tapa sinangag itlog",
        "tosilog" to "tocino sinangag itlog",
        "longsilog" to "longganisa sinangag itlog"
    )

    // Stop words for canonical key generation (Lower case)
    val QUANTITY_STOP_WORDS = setOf(
        "a", "an", "the", "of", "some", "few",
        "large", "medium", "small",
        "slice", "slices",
        "piece", "pieces", "pcs", "pc",
        "bowl", "bowls",
        "cup", "cups",
        "serving", "servings",
        "whole", "half", "quarter"
    )
}
