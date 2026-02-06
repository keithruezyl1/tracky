package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.ExerciseEntryDao
import com.tracky.app.data.local.dao.FoodEntryDao
import com.tracky.app.data.local.dao.MacroTotals
import com.tracky.app.data.mapper.toDomain
import com.tracky.app.data.mapper.toEntity
import com.tracky.app.domain.model.DailySummary
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingRepository @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val exerciseEntryDao: ExerciseEntryDao,
    private val goalRepository: GoalRepository
) {
    // ─────────────────────────────────────────────────────────────────────────
    // Food Entries
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get food entries for a date
     */
    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>> {
        return foodEntryDao.getEntriesForDate(date).map { entries ->
            entries.map { entry ->
                val items = foodEntryDao.getItemsForEntryOnce(entry.id)
                entry.toDomain(items)
            }
        }
    }

    /**
     * Get single food entry by ID
     */
    suspend fun getFoodEntryById(id: Long): FoodEntry? {
        val entry = foodEntryDao.getEntryById(id) ?: return null
        val items = foodEntryDao.getItemsForEntryOnce(id)
        return entry.toDomain(items)
    }

    /**
     * Save food entry with items
     */
    suspend fun saveFoodEntry(entry: FoodEntry): Long {
        val entryEntity = entry.toEntity()
        val itemEntities = entry.items.mapIndexed { index, item ->
            item.toEntity(0).copy(displayOrder = index)
        }
        return foodEntryDao.insertEntryWithItems(entryEntity, itemEntities)
    }

    /**
     * Update food entry
     */
    suspend fun updateFoodEntry(entry: FoodEntry) {
        foodEntryDao.update(entry.toEntity())
        // Update items
        foodEntryDao.deleteItemsForEntry(entry.id)
        val itemEntities = entry.items.map { it.toEntity(entry.id) }
        foodEntryDao.insertItems(itemEntities)
    }

    /**
     * Delete food entry
     */
    suspend fun deleteFoodEntry(id: Long) {
        foodEntryDao.deleteEntryWithItems(id)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Exercise Entries
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get exercise entries for a date
     */
    fun getExerciseEntriesForDate(date: String): Flow<List<ExerciseEntry>> {
        return exerciseEntryDao.getEntriesForDate(date).map { entries ->
            entries.map { it.entry.toDomain(it.items) }
        }
    }

    /**
     * Get single exercise entry by ID
     */
    suspend fun getExerciseEntryById(id: Long): ExerciseEntry? {
        return exerciseEntryDao.getEntryById(id)?.let {
            it.entry.toDomain(it.items)
        }
    }

    /**
     * Save exercise entry
     */
    suspend fun saveExerciseEntry(entry: ExerciseEntry): Long {
        val entryId = exerciseEntryDao.insert(entry.toEntity())
        val itemEntities = entry.items.mapIndexed { index, item ->
            item.toEntity(entryId).copy(displayOrder = index)
        }
        exerciseEntryDao.insertItems(itemEntities)
        return entryId
    }

    /**
     * Update exercise entry
     */
    suspend fun updateExerciseEntry(entry: ExerciseEntry) {
        exerciseEntryDao.update(entry.toEntity())
        
        // Update items (replace-all strategy for simplicity)
        exerciseEntryDao.deleteItemsForEntry(entry.id)
        val itemEntities = entry.items.mapIndexed { index, item ->
            item.toEntity(entry.id).copy(displayOrder = index)
        }
        exerciseEntryDao.insertItems(itemEntities)
    }

    /**
     * Delete exercise entry
     */
    suspend fun deleteExerciseEntry(id: Long) {
        exerciseEntryDao.deleteById(id)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Daily Totals
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get total food calories for a date
     */
    fun getTotalFoodCalories(date: String): Flow<Float> {
        return foodEntryDao.getTotalCaloriesForDate(date).map { it ?: 0f }
    }

    /**
     * Get total exercise calories for a date
     */
    fun getTotalExerciseCalories(date: String): Flow<Float> {
        return exerciseEntryDao.getTotalCaloriesBurnedForDate(date).map { it ?: 0f }
    }

    /**
     * Get macro totals for a date
     */
    fun getMacroTotals(date: String): Flow<MacroTotals?> {
        return foodEntryDao.getMacroTotalsForDate(date)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Range Summaries (for Summary Screen)
    // ─────────────────────────────────────────────────────────────────────────

    fun getTotalFoodCaloriesBetween(startDate: String, endDate: String): Flow<Float> {
        return foodEntryDao.getTotalCaloriesBetween(startDate, endDate).map { it ?: 0f }
    }

    fun getTotalExerciseCaloriesBetween(startDate: String, endDate: String): Flow<Float> {
        return exerciseEntryDao.getTotalCaloriesBurnedBetween(startDate, endDate).map { it ?: 0f }
    }

    fun getMacroTotalsBetween(startDate: String, endDate: String): Flow<MacroTotals?> {
        return foodEntryDao.getMacroTotalsBetween(startDate, endDate)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Daily Summary
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get complete daily summary combining all data
     */
    fun getDailySummary(date: String): Flow<DailySummary> {
        return combine(
            goalRepository.getGoalForDate(date),
            getFoodEntriesForDate(date),
            getExerciseEntriesForDate(date),
            getTotalFoodCalories(date),
            getTotalExerciseCalories(date),
            getMacroTotals(date)
        ) { values ->
            @Suppress("UNCHECKED_CAST")
            val goal = values[0] as? com.tracky.app.domain.model.DailyGoal
            val foodEntries = values[1] as List<FoodEntry>
            val exerciseEntries = values[2] as List<ExerciseEntry>
            val foodCalories = values[3] as Float
            val exerciseCalories = values[4] as Float
            val macros = values[5] as? MacroTotals

            DailySummary(
                date = date,
                goal = goal,
                foodCalories = foodCalories,
                exerciseCalories = exerciseCalories,
                carbsConsumedG = macros?.carbs ?: 0f,
                proteinConsumedG = macros?.protein ?: 0f,
                fatConsumedG = macros?.fat ?: 0f,
                foodEntries = foodEntries,
                exerciseEntries = exerciseEntries
            )
        }
    }
}
