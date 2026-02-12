package com.tracky.app.domain.resolver

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates strict canonical keys for food items.
 *
 * Pipeline (in order):
 * 1. Lowercase & trim
 * 2. Strip punctuation
 * 3. Collapse whitespace
 * 4. Singularize simple plurals
 * 5. Expand Filipino/brand synonyms
 * 6. Remove quantity stop words
 * 7. Join remaining tokens (NO token sorting — word order matters)
 */
@Singleton
class CanonicalKeyGenerator @Inject constructor() {

    fun generate(rawName: String): String {
        if (rawName.isBlank()) return ""

        // 1. Lowercase & basic clean
        var processed = rawName.trim().lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s]"), "") // Remove special chars
            .replace(Regex("\\s+"), " ") // Collapse spaces

        // 2. Singularize simple plurals (eggs→egg, slices→slice)
        processed = processed.split(" ").joinToString(" ") { token ->
            singularize(token)
        }

        // 3. Expand Synonyms (from Config)
        FoodResolutionConfig.SYNONYM_MAP.forEach { (term, expansion) ->
            if (processed.contains(term)) {
                processed = processed.replace(term, expansion)
            }
        }

        // 4. Tokenize
        val tokens = processed.split(" ").filter { it.isNotBlank() }

        // 5. Remove Stop Words — do NOT sort (word order matters)
        val canonicalTokens = tokens
            .filter { !FoodResolutionConfig.QUANTITY_STOP_WORDS.contains(it) }

        // 6. Join
        return canonicalTokens.joinToString(" ")
    }

    /**
     * Naive English singularization for food terms.
     * Handles common suffixes only.
     */
    private fun singularize(word: String): String {
        if (word.length <= 3) return word
        return when {
            word.endsWith("ies") && !word.endsWith("series") ->
                word.dropLast(3) + "y" // berries→berry
            word.endsWith("ses") ->
                word // leave ambiguous cases as-is (sauces, buses)
            word.endsWith("es") && (word.endsWith("ches") || word.endsWith("shes")) ->
                word.dropLast(2) // sandwiches→sandwich
            word.endsWith("s") && !word.endsWith("ss") ->
                word.dropLast(1) // eggs→egg, bananas→banana
            else -> word
        }
    }
}
