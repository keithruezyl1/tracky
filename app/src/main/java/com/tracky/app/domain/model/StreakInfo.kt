package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Explicit states for the streak system
 */
@Serializable
enum class StreakStatus(val value: String) {
    /** Current streak is 0. Fire is outline only. */
    NONE("none"),
    
    /** Yesterday was active/grace, but today has no qualifying log yet. Fire is outline with current count. */
    AT_RISK("at_risk"),
    
    /** Today's commitment met (>= 1 qualifying entry). Fire is colored with count. */
    ACTIVE("active"),
    
    /** Yesterday missed, grace day consumed. Visual indicator shows "Ice" or "Frozen" state on the flame. */
    FROZEN("frozen");

    companion object {
        fun fromValue(value: String?): StreakStatus {
            return entries.find { it.value == value } ?: NONE
        }
    }
}

/**
 * Domain model representing the current streak information
 */
@Serializable
data class StreakInfo(
    val count: Int = 0,
    val status: StreakStatus = StreakStatus.NONE,
    val lastGraceDate: String? = null,
    val isPerfectDay: Boolean = false,
    val nextGraceAvailableInDays: Int = 0
)
