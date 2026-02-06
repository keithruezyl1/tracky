package com.tracky.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExerciseIntensity(val value: String) {
    LOW("low"),
    MODERATE("moderate"),
    HIGH("high");

    companion object {
        fun fromValue(value: String?): ExerciseIntensity? {
            return entries.find { it.value == value }
        }
    }
}
