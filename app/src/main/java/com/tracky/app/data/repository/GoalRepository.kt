package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.DailyGoalDao
import com.tracky.app.data.mapper.toDomain
import com.tracky.app.data.mapper.toEntity
import com.tracky.app.domain.model.DailyGoal
import com.tracky.app.domain.model.MacroDistribution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val dailyGoalDao: DailyGoalDao
) {
    /**
     * Get current goal for today
     */
    fun getCurrentGoal(): Flow<DailyGoal?> {
        val today = getTodayDate()
        return dailyGoalDao.getCurrentGoal(today).map { it?.toDomain() }
    }

    /**
     * Get current goal for a specific date
     */
    fun getGoalForDate(date: String): Flow<DailyGoal?> {
        return dailyGoalDao.getCurrentGoal(date).map { it?.toDomain() }
    }

    /**
     * Get current goal once (suspend)
     */
    suspend fun getCurrentGoalOnce(): DailyGoal? {
        val today = getTodayDate()
        return dailyGoalDao.getCurrentGoalOnce(today)?.toDomain()
    }

    /**
     * Get goal for specific date once
     */
    suspend fun getGoalForDateOnce(date: String): DailyGoal? {
        return dailyGoalDao.getCurrentGoalOnce(date)?.toDomain()
    }

    /**
     * Get all goals history
     */
    fun getAllGoals(): Flow<List<DailyGoal>> {
        return dailyGoalDao.getAllGoals().map { list -> list.map { it.toDomain() } }
    }

    /**
     * Save new goal (effective from today)
     */
    suspend fun saveGoal(
        calorieGoalKcal: Float,
        macroDistribution: MacroDistribution
    ): Long {
        require(macroDistribution.isValid) { "Macro percentages must sum to 100" }

        val targets = DailyGoal.calculateMacroTargets(
            calorieGoalKcal,
            macroDistribution.carbsPct,
            macroDistribution.proteinPct,
            macroDistribution.fatPct
        )

        val goal = DailyGoal(
            effectiveFromDate = getTodayDate(),
            calorieGoalKcal = calorieGoalKcal,
            carbsPct = macroDistribution.carbsPct,
            proteinPct = macroDistribution.proteinPct,
            fatPct = macroDistribution.fatPct,
            carbsTargetG = targets.carbsG,
            proteinTargetG = targets.proteinG,
            fatTargetG = targets.fatG,
            createdAt = System.currentTimeMillis()
        )

        return dailyGoalDao.insert(goal.toEntity())
    }

    /**
     * Update existing goal
     */
    suspend fun updateGoal(goal: DailyGoal) {
        dailyGoalDao.update(goal.toEntity())
    }

    /**
     * Check if any goals exist
     */
    suspend fun hasAnyGoals(): Boolean {
        return dailyGoalDao.hasAnyGoals()
    }

    private fun getTodayDate(): String {
        val now = Clock.System.now()
        val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return localDate.toString() // ISO format: YYYY-MM-DD
    }
}
