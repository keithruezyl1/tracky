package com.tracky.app.domain.logic

import com.tracky.app.domain.model.ExerciseItem
import com.tracky.app.domain.model.FoodItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Detects material differences between nutrition values to determine if a "Review Update"
 * dialog is necessary.
 *
 * Rules:
 * - Difference >= 10 kcal OR >= 5% (whichever is larger)
 * - Difference >= 2g OR >= 10% for macros (Carbs/Protein/Fat)
 */
@Singleton
class NutritionConflictDetector @Inject constructor() {

    fun isMaterialDifference(current: FoodItem, new: FoodItem): Boolean {
        // 1. Calories
        if (isDiffSignificant(current.calories, new.calories, 10f, 0.05f)) return true

        // 2. Macros
        if (isDiffSignificant(current.carbsG, new.carbsG, 2f, 0.10f)) return true
        if (isDiffSignificant(current.proteinG, new.proteinG, 2f, 0.10f)) return true
        if (isDiffSignificant(current.fatG, new.fatG, 2f, 0.10f)) return true

        return false
    }

    fun isMaterialDifference(current: ExerciseItem, new: ExerciseItem): Boolean {
        // 1. Calories
        if (isDiffSignificant(current.caloriesBurned, new.caloriesBurned, 10f, 0.05f)) return true
        
        // 2. MET Value (if relevant, though usually secondary to calories)
        // We can treat MET changes as material if they result in significant calorie changes,
        // so checking calories is usually sufficient. But if we want to be strict:
        if (abs(current.metValue - new.metValue) > 0.5f) return true

        return false
    }

    private fun isDiffSignificant(val1: Float, val2: Float, absThreshold: Float, relThreshold: Float): Boolean {
        val diff = abs(val1 - val2)
        // Rule: Difference >= absThreshold OR >= relThreshold
        // Does "OR" mean even if diff is small (e.g. 1 vs 2 kcal = 100%), it should trigger?
        // Usually NO, we want a floor. "Material" implies it matters. 1kcal doesn't matter.
        // So logic: Must be >= absThreshold TO EVEN CONSIDER it material? 
        // OR does it mean: if I have 1000kcal vs 1005kcal (diff 5), is it material? No.
        // If I have 1000 vs 1060 (diff 60 > 10). Rel = 6%. Material.
        
        // Let's implement:
        // Return true if (Diff >= AbsThreshold)
        // Return true if (RelDiff >= RelThreshold AND Diff >= AbsThreshold) -- wait, that's redundant.
        
        // Actually, sometimes "OR >= 5%" is for large numbers.
        // Example: 100 vs 106. Diff = 6. AbsThreshold=10. 
        // If strict OR: 6 < 10 (False). Rel = 6/106 = 5.6% (True). -> TRUE?
        // If we want 100 vs 106 to be material, then purely checking AbsThreshold first as a blocker is wrong.
        
        // Correction: We likely want a minimum floor for "noise" (e.g. < 5kcal is never material).
        // But maybe 10kcal is too high a floor for "OR". 
        // Let's say floor is smaller, e.g. 1.0.
        
        if (diff < 1.0f) return false // Noise floor
        
        if (diff >= absThreshold) return true
        
        val maxVal = maxOf(val1, val2)
        if (maxVal == 0f) return false // Should have been caught by diff check if diff > 1
        
        val relDiff = diff / maxVal
        return relDiff >= relThreshold
    }
}
