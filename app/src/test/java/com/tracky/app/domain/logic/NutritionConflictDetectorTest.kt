package com.tracky.app.domain.logic

import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionConflictDetectorTest {

    private val detector = NutritionConflictDetector()
    private val baseProvenance = Provenance(ProvenanceSource.DATASET, null, 1f)

    @Test
    fun `isMaterialDifference food - no difference`() {
        val old = createFoodItem(100f, 10f, 10f, 10f)
        val new = createFoodItem(100f, 10f, 10f, 10f)
        assertFalse(detector.isMaterialDifference(old, new))
    }

    @Test
    fun `isMaterialDifference food - small difference ignored`() {
        // Less than 10kcal diff
        assertFalse(detector.isMaterialDifference(createFoodItem(100f), createFoodItem(109f)))
        // Less than 2g macro diff
        assertFalse(detector.isMaterialDifference(createFoodItem(100f, 10f), createFoodItem(100f, 11.5f)))
    }

    @Test
    fun `isMaterialDifference food - calories absolute difference`() {
        // >= 10 kcal diff
        assertTrue(detector.isMaterialDifference(createFoodItem(100f), createFoodItem(110f)))
        assertTrue(detector.isMaterialDifference(createFoodItem(100f), createFoodItem(90f)))
    }

    @Test
    fun `isMaterialDifference food - calories relative difference`() {
        // 100 vs 105 is 5% -> material (and < 10 abs, but we check OR condition usually? 
        // Logic says: diff >= 10kcal OR >= 5%
        // Actually wait, let's check code logic:
        // if (diff < absThreshold) return false
        // So it MUST be at least 10kcal diff to even consider relative check?
        // Ah, logic:
        // val diff = abs(val1 - val2)
        // if (diff < absThreshold) return false 
        // So 100 vs 105 (diff=5) returns false immediately.
        
        // So for large numbers: 1000 vs 1050 (diff=50, >10). Relative = 50/1050 = 4.7% -> False
        // 1000 vs 1060 (diff=60). Relative = 60/1060 = 5.6% -> True
        
        assertFalse(detector.isMaterialDifference(createFoodItem(1000f), createFoodItem(1050f)))
        assertTrue(detector.isMaterialDifference(createFoodItem(1000f), createFoodItem(1060f)))
    }

    @Test
    fun `isMaterialDifference food - macro difference`() {
        // >= 2g diff
        assertTrue(detector.isMaterialDifference(createFoodItem(100f, 10f), createFoodItem(100f, 12.1f)))
        // 10g vs 11g (diff=1) -> False
        assertFalse(detector.isMaterialDifference(createFoodItem(100f, 10f), createFoodItem(100f, 11f)))
    }

    private fun createFoodItem(
        calories: Float = 0f,
        carbs: Float = 0f,
        protein: Float = 0f,
        fat: Float = 0f
    ): FoodItem {
        return FoodItem(
            id = 0,
            name = "Test",
            quantity = 1f,
            unit = "serving",
            calories = calories,
            carbsG = carbs,
            proteinG = protein,
            fatG = fat,
            provenance = baseProvenance,
            displayOrder = 0, 
            canonicalKey = "test",
            isManualMacros = false,
            isAnalyzing = false
        )
    }
}
