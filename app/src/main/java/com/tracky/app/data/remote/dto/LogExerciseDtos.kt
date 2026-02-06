package com.tracky.app.data.remote.dto

import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// Log Exercise Request/Response
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class LogExerciseRequest(
    val text: String,
    val userWeightKg: Float,
    val durationMinutes: Int? = null
)

@Serializable
data class LogExerciseResponse(
    val status: String,
    val exercises: List<ParsedExerciseDto> = emptyList(),
    val requiresConfirmation: Boolean,
    val error: String? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// Resolve Exercise Request/Response
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class ResolveExerciseRequest(
    val activity: String,
    val durationMinutes: Int,
    val userWeightKg: Float,
    val metValue: Float? = null
)

@Serializable
data class ResolveExerciseResponse(
    val activity: String,
    val durationMinutes: Int,
    val metValue: Float? = null,
    val caloriesBurned: Int? = null,
    val userWeightKg: Float? = null,
    val source: String? = null,
    val formula: String? = null,
    val resolved: Boolean,
    val requiresManualEntry: Boolean? = null,
    val availableActivities: List<String>? = null,
    val message: String? = null,
    val error: String? = null
)
