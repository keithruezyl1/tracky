package com.tracky.app.domain.resolver

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates strict canonical keys for food items.
 * 
 * Logic:
 * 1. Lowercase & Normalize
 * 2. Remove Quantity Words ("large", "small", "slice", etc.)
 * 3. Expand Synonyms (e.g. "sinangag" -> "garlic fried rice")
 * 4. Sort Tokens (Alphabetical order)
 * 5. Join to create stable key
 */
@Singleton
class CanonicalKeyGenerator @Inject constructor() {

    fun generate(rawName: String): String {
        if (rawName.isBlank()) return ""

        // 1. Lowercase & Basic Clean
        var processed = rawName.trim().lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s]"), "") // Remove special chars
            .replace(Regex("\\s+"), " ") // Collapse spaces

        // 2. Expand Synonyms (from Config)
        // Check for synonyms in the processed text
        FoodResolutionConfig.SYNONYM_MAP.forEach { (term, expansion) ->
            if (processed.contains(term)) {
                processed = processed.replace(term, expansion)
            }
        }

        // 3. Tokenize
        val tokens = processed.split(" ").filter { it.isNotBlank() }

        // 4. Remove Stop Words (from Config) & Sort
        val canonicalTokens = tokens
            .filter { !FoodResolutionConfig.QUANTITY_STOP_WORDS.contains(it) }
            .sorted()

        // 5. Join
        return canonicalTokens.joinToString(" ")
    }
}
