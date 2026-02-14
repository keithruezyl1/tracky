package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.ExerciseEntryDao
import com.tracky.app.data.local.dao.FoodEntryDao
import com.tracky.app.data.local.dao.MacroTotals
import com.tracky.app.data.local.dao.DailyLogSummaryDao
import com.tracky.app.data.mapper.toDomain
import com.tracky.app.data.mapper.toEntity
import com.tracky.app.domain.model.DailySummary
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.usecase.StreakInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingRepository @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val exerciseEntryDao: ExerciseEntryDao,
    private val dailyLogSummaryDao: DailyLogSummaryDao,
    private val streakInteractor: StreakInteractor,
    private val goalRepository: GoalRepository,
    private val reanalysisBackupDao: com.tracky.app.data.local.dao.ReanalysisBackupDao
) {
    private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

    // ─────────────────────────────────────────────────────────────────────────
    // Backups
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun saveFoodBackup(entry: FoodEntry) {
        val backup = com.tracky.app.data.local.entity.ReanalysisBackupEntity(
            originalEntryId = entry.id,
            type = "food",
            dataJson = json.encodeToString(FoodEntry.serializer(), entry),
            date = entry.date
        )
        reanalysisBackupDao.insertBackup(backup)
    }

    suspend fun saveExerciseBackup(entry: ExerciseEntry) {
        val backup = com.tracky.app.data.local.entity.ReanalysisBackupEntity(
            originalEntryId = entry.id,
            type = "exercise",
            dataJson = json.encodeToString(ExerciseEntry.serializer(), entry),
            date = entry.date
        )
        reanalysisBackupDao.insertBackup(backup)
    }

    suspend fun getFoodBackup(id: Long): FoodEntry? {
        val backup = reanalysisBackupDao.getBackup(id, "food")
        return backup?.let { json.decodeFromString(FoodEntry.serializer(), it.dataJson) }
    }

    suspend fun getExerciseBackup(id: Long): ExerciseEntry? {
        val backup = reanalysisBackupDao.getBackup(id, "exercise")
        return backup?.let { json.decodeFromString(ExerciseEntry.serializer(), it.dataJson) }
    }

    suspend fun deleteBackup(id: Long, type: String) {
        reanalysisBackupDao.deleteBackup(id, type)
    }
    // ─────────────────────────────────────────────────────────────────────────
    // Food Entries
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get food entries for a date
     */
    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>> {
        return foodEntryDao.getEntriesWithItemsForDate(date).map { entities ->
            entities.map { it.entry.toDomain(it.items) }
        }
    }

    /**
     * Get single food entry by ID
     */
    suspend fun getFoodEntryById(id: Long): FoodEntry? {
        return foodEntryDao.getEntryWithItemsByIdOnce(id)?.let {
            it.entry.toDomain(it.items)
        }
    }

    /**
     * Save food entry with items
     */
    suspend fun saveFoodEntry(entry: FoodEntry): Long {
        val entryEntity = entry.toEntity()
        val entryId = foodEntryDao.insert(entryEntity)
        val itemEntities = entry.items.mapIndexed { index, item ->
            item.toEntity(entryId).copy(displayOrder = index)
        }
        foodEntryDao.insertItems(itemEntities)
        updateDailyLogSummary(entry.date)
        return entryId
    }

    /**
     * Update food entry
     */
    suspend fun updateFoodEntry(entry: FoodEntry) {
        val itemEntities = entry.items.map { it.toEntity(entry.id) }
        foodEntryDao.updateEntryWithItems(entry.toEntity(), itemEntities)
        updateDailyLogSummary(entry.date)
    }

    /**
     * Delete food entry
     */
    suspend fun deleteFoodEntry(id: Long) {
        val entry = getFoodEntryById(id)
        foodEntryDao.deleteEntryWithItems(id)
        entry?.let { updateDailyLogSummary(it.date) }
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
        updateDailyLogSummary(entry.date)
        return entryId
    }

    /**
     * Update exercise entry
     */
    suspend fun updateExerciseEntry(entry: ExerciseEntry) {
        // Update items (replace-all strategy for simplicity)
        val itemEntities = entry.items.mapIndexed { index, item ->
            item.toEntity(entry.id).copy(displayOrder = index)
        }
        exerciseEntryDao.updateEntryWithItems(entry.toEntity(), itemEntities)
        updateDailyLogSummary(entry.date)
    }

    /**
     * Delete exercise entry
     */
    suspend fun deleteExerciseEntry(id: Long) {
        val entry = getExerciseEntryById(id)
        exerciseEntryDao.deleteEntryWithItems(id)
        entry?.let { updateDailyLogSummary(it.date) }
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

    fun getDailySummary(date: String): Flow<DailySummary> {
        return combine(
            goalRepository.getGoalForDate(date),
            getFoodEntriesForDate(date),
            getExerciseEntriesForDate(date),
            getTotalFoodCalories(date),
            getTotalExerciseCalories(date),
            getMacroTotals(date)
        ) { values ->
            val goal = values[0] as? com.tracky.app.domain.model.DailyGoal
            @Suppress("UNCHECKED_CAST")
            val foodEntries = (values[1] as? List<FoodEntry>) ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val exerciseEntries = (values[2] as? List<ExerciseEntry>) ?: emptyList()
            val foodCalories = (values[3] as? Float) ?: 0f
            val exerciseCalories = (values[4] as? Float) ?: 0f
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
    /**
     * Recompute daily log summary for a date, used for optimized streak checks.
     */
    private suspend fun updateDailyLogSummary(date: String) {
        val foodKcal = foodEntryDao.getTotalCaloriesForDateOnce(date) ?: 0f
        val exerciseKcal = exerciseEntryDao.getTotalCaloriesBurnedForDateOnce(date) ?: 0f
        val goal = goalRepository.getGoalForDateOnce(date)
        
        // Qualification: >= 1 qualifying entry (>= 10 kcal)
        // We need to count items directly in the DAO or just check totals for now.
        // PRD says: "Commitment based: user logs >= 1 qualifying entry (kcal >= 10)"
        
        val foodItems = foodEntryDao.getFoodItemsForDateOnce(date)
        val exerciseItems = exerciseEntryDao.getExerciseItemsForDateOnce(date)
        
        val qualifyingCount = foodItems.count { it.calories >= 10f } + 
                           exerciseItems.count { it.caloriesBurned >= 10f }
        
        val targetKcal = goal?.calorieGoalKcal ?: 2000f
        val metGoal = (targetKcal - foodKcal + exerciseKcal) >= 0

        dailyLogSummaryDao.insert(
            com.tracky.app.data.local.entity.DailyLogSummaryEntity(
                date = date,
                qualifyingEntriesCount = qualifyingCount,
                totalCaloriesConsumed = foodKcal,
                totalCaloriesBurned = exerciseKcal,
                metGoal = metGoal
            )
        )
        
        // Recompute official streak count/state
        streakInteractor.calculateStreak()
    }
}
