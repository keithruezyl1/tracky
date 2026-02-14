package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for Exercise Item
 */
@Serializable
data class ExerciseItem(
    val id: Long = 0,
    val activityName: String,
    val durationMinutes: Int,
    val metValue: Float,
    val caloriesBurned: Float,
    val intensity: ExerciseIntensity?,
    val provenance: Provenance,
    val displayOrder: Int,
    
    /**
     * Whether values (calories/intensity) were manually edited.
     * If true, AI auto-reanalysis will NOT overwrite.
     */
    val isManual: Boolean = false,

    /**
     * Monotonic revision number for analysis.
     */
    val analysisRevision: Long = 0,

    /**
     * Pending suggested update from AI.
     */
    val pendingSuggestion: ExerciseItem? = null,

    /**
     * Transient state for UI loading indicator.
     * Not serialized.
     */
    @kotlinx.serialization.Transient
    val isAnalyzing: Boolean = false
)
