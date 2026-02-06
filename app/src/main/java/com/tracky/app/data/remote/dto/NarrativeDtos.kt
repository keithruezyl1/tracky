package com.tracky.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenerateNarrativeRequest(
    val items: List<NarrativeItemDto>,
    val totals: MacroTotalsDto,
    val entryType: String // "food" or "exercise"
)

@Serializable
data class NarrativeItemDto(
    val name: String,
    val quantity: Float,
    val unit: String,
    val calories: Int
)

@Serializable
data class GenerateNarrativeResponse(
    val narrative: String
)
