package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.WeightEntryDao
import com.tracky.app.data.mapper.toDomain
import com.tracky.app.data.mapper.toEntity
import com.tracky.app.domain.model.WeightChartRange
import com.tracky.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepository @Inject constructor(
    private val weightEntryDao: WeightEntryDao
) {
    /**
     * Get all weight entries
     */
    fun getAllEntries(): Flow<List<WeightEntry>> {
        return weightEntryDao.getAllEntries().map { list -> list.map { it.toDomain() } }
    }

    /**
     * Get entries for a date range (for chart)
     */
    fun getEntriesForRange(range: WeightChartRange): Flow<List<WeightEntry>> {
        val (startDate, endDate) = getDateRangeForChartRange(range)
        return weightEntryDao.getEntriesInRange(startDate, endDate)
            .map { list -> list.map { it.toDomain() } }
    }

    /**
     * Get entries for date range once
     */
    suspend fun getEntriesForRangeOnce(range: WeightChartRange): List<WeightEntry> {
        val (startDate, endDate) = getDateRangeForChartRange(range)
        return weightEntryDao.getEntriesInRangeOnce(startDate, endDate)
            .map { it.toDomain() }
    }

    /**
     * Get latest weight entry
     */
    fun getLatestEntry(): Flow<WeightEntry?> {
        return weightEntryDao.getLatestEntry().map { it?.toDomain() }
    }

    /**
     * Get latest weight entry once
     */
    suspend fun getLatestEntryOnce(): WeightEntry? {
        return weightEntryDao.getLatestEntryOnce()?.toDomain()
    }

    /**
     * Add weight entry
     */
    suspend fun addEntry(weightKg: Float, date: String, note: String? = null): Long {
        val now = System.currentTimeMillis()
        val entry = WeightEntry(
            date = date,
            weightKg = weightKg,
            note = note,
            timestamp = now,
            createdAt = now,
            updatedAt = now
        )
        return weightEntryDao.insert(entry.toEntity())
    }

    /**
     * Update weight entry
     */
    suspend fun updateEntry(entry: WeightEntry) {
        val updatedEntry = entry.copy(updatedAt = System.currentTimeMillis())
        weightEntryDao.update(updatedEntry.toEntity())
    }

    /**
     * Delete weight entry
     */
    suspend fun deleteEntry(id: Long) {
        weightEntryDao.deleteById(id)
    }

    /**
     * Get entry for a specific date
     */
    suspend fun getEntryForDate(date: String): WeightEntry? {
        return weightEntryDao.getEntryForDate(date)?.toDomain()
    }

    /**
     * Get entry count
     */
    suspend fun getEntryCount(): Int {
        return weightEntryDao.getEntryCount()
    }

    /**
     * Calculate date range for chart range
     */
    private fun getDateRangeForChartRange(range: WeightChartRange): Pair<String, String> {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val endDate = today.toString()

        val startDate = when (range) {
            WeightChartRange.WEEK -> today.minus(7, DateTimeUnit.DAY).toString()
            WeightChartRange.MONTH -> today.minus(1, DateTimeUnit.MONTH).toString()
            WeightChartRange.YEAR -> today.minus(1, DateTimeUnit.YEAR).toString()
            WeightChartRange.ALL -> "1970-01-01" // Start from beginning
        }

        return startDate to endDate
    }
}
