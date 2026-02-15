package com.tracky.app.util

import java.util.Locale

fun String.toTitleCase(): String {
    if (this.isEmpty()) return this
    
    val smallWords = setOf(
        "a", "an", "and", "as", "at", "but", "by", "for", "if", "in", "nor", "of", "on", "or", "so", "the", "to", "up", "yet", "with"
    )
    
    val words = this.trim().split("\\s+".toRegex())
    return words.mapIndexed { index, word ->
        if (word.isEmpty()) return@mapIndexed ""
        val lowerWord = word.lowercase()
        if (index == 0 || index == words.lastIndex || !smallWords.contains(lowerWord)) {
            lowerWord.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } else {
            lowerWord
        }
    }.joinToString(" ")
}
