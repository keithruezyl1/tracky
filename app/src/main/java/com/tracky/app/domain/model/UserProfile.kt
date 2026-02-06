package com.tracky.app.domain.model

/**
 * Domain model for User Profile
 */
data class UserProfile(
    val heightCm: Float,
    val currentWeightKg: Float,
    val targetWeightKg: Float,
    val unitPreference: UnitPreference,
    val timezone: String,
    val bmi: Float,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        /**
         * Calculate BMI: weight(kg) / height(m)^2
         */
        fun calculateBmi(weightKg: Float, heightCm: Float): Float {
            val heightM = heightCm / 100f
            return if (heightM > 0) weightKg / (heightM * heightM) else 0f
        }

        /**
         * Get BMI classification based on WHO standards
         * @return Pair of (classification name, color suggestion)
         */
        fun getBmiClassification(bmi: Float): BmiClassification {
            return when {
                bmi <= 0 -> BmiClassification.UNKNOWN
                bmi < 18.5f -> BmiClassification.UNDERWEIGHT
                bmi < 25f -> BmiClassification.NORMAL
                bmi < 30f -> BmiClassification.OVERWEIGHT
                bmi < 35f -> BmiClassification.OBESE_CLASS_1
                bmi < 40f -> BmiClassification.OBESE_CLASS_2
                else -> BmiClassification.OBESE_CLASS_3
            }
        }
    }
}

/**
 * BMI Classification based on WHO standards
 */
enum class BmiClassification(val label: String, val description: String) {
    UNKNOWN("Unknown", ""),
    UNDERWEIGHT("Underweight", "BMI < 18.5"),
    NORMAL("Normal", "BMI 18.5-24.9"),
    OVERWEIGHT("Overweight", "BMI 25-29.9"),
    OBESE_CLASS_1("Obese (Class I)", "BMI 30-34.9"),
    OBESE_CLASS_2("Obese (Class II)", "BMI 35-39.9"),
    OBESE_CLASS_3("Obese (Class III)", "BMI ≥ 40")
}

/**
 * Unit preference for display
 */
enum class UnitPreference(val value: String) {
    METRIC("metric"),
    IMPERIAL("imperial");

    companion object {
        fun fromValue(value: String): UnitPreference {
            return entries.find { it.value == value } ?: METRIC
        }
    }
}
