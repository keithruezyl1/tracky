package com.tracky.app.domain.resolver

/**
 * Configuration constants for food resolution logic.
 * 
 * Defines strict thresholds for data provenance and quality reuse.
 */
object FoodResolutionConfig {
    // USDA is authoritative; very high confidence required to be treated as such automatically
    const val USDA_MIN_CONFIDENCE = 0.9f
    
    // History reuse requires high similarity and quality; stricter than USDA to prevent drift
    const val HISTORY_MIN_REUSE_CONFIDENCE = 0.8f
    
    // Internet results must meet this bar plus macro completeness checks
    const val SERP_MIN_CONFIDENCE = 0.7f
    
    // Hybrid matching weights
    const val SCORE_WEIGHT_JACCARD = 0.5f
    const val SCORE_WEIGHT_LEVENSHTEIN = 0.5f

    // Shared Synonym Map (Lower case keys)
    val SYNONYM_MAP = mapOf(
        "sinangag" to "garlic fried rice",
        "taho" to "silken tofu arnibal",
        "champorado" to "chocolate rice porridge",
        "chickenjoy" to "fried chicken",
        "pandesal" to "bread roll",
        "itlog" to "egg",
        "kanin" to "rice",
        "sinaing" to "steamed rice"
    )

    // Stop words for canonical key generation (Lower case)
    val QUANTITY_STOP_WORDS = setOf(
        "large", "medium", "small",
        "slice", "slices",
        "piece", "pieces", "pcs", "pc",
        "bowl", "bowls",
        "cup", "cups",
        "serving", "servings",
        "whole", "half", "quarter"
    )

    // Valid units for Internet results (Lower case)
    // We reject "serving", "unknown" etc if they don't map to a real visual quantity,
    // matches what we generally accept in the UI.
    val VALID_UNITS = setOf(
        "g", "gram", "grams",
        "oz", "ounce", "ounces",
        "ml", "milliliter", "milliliters",
        "cup", "cups",
        "tbsp", "tablespoon", "tablespoons",
        "tsp", "teaspoon", "teaspoons",
        "slice", "slices",
        "piece", "pieces",
        "serving", "servings" // We accept 'serving' if macros are complete, though less ideal
    )
}
