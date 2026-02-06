package com.tracky.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileTest {

    // ─────────────────────────────────────────────────────────────────────────────
    // BMI Calculation Tests
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    fun calculateBmi_isCorrect() {
        // Given: 80kg, 180cm (1.8m) -> 80 / (1.8 * 1.8) = 24.691358
        val weight = 80f
        val height = 180f
        
        val result = UserProfile.calculateBmi(weight, height)
        
        assertEquals(24.69f, result, 0.01f)
    }

    @Test
    fun calculateBmi_zeroHeight_returnsZero() {
        val result = UserProfile.calculateBmi(70f, 0f)
        assertEquals(0f, result, 0.0f)
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // BMI Classification Tests
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    fun getBmiClassification_underweight() {
        val result = UserProfile.getBmiClassification(18.0f)
        assertEquals(BmiClassification.UNDERWEIGHT, result)
    }

    @Test
    fun getBmiClassification_normal() {
        val result = UserProfile.getBmiClassification(22.0f)
        assertEquals(BmiClassification.NORMAL, result)
    }

    @Test
    fun getBmiClassification_overweight() {
        val result = UserProfile.getBmiClassification(27.0f)
        assertEquals(BmiClassification.OVERWEIGHT, result)
    }

    @Test
    fun getBmiClassification_obeseClass1() {
        val result = UserProfile.getBmiClassification(32.0f)
        assertEquals(BmiClassification.OBESE_CLASS_1, result)
    }
}
