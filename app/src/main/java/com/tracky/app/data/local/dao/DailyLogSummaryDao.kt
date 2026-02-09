package com.tracky.app.data.local.dao

import androidx.room.*
import com.tracky.app.data.local.entity.DailyLogSummaryEntity

@Dao
interface DailyLogSummaryDao {
    @Query("SELECT * FROM daily_log_summaries WHERE date = :date")
    suspend fun getSummaryForDate(date: String): DailyLogSummaryEntity?

    @Query("SELECT * FROM daily_log_summaries WHERE date <= :date ORDER BY date DESC LIMIT :limit")
    suspend fun getSummariesBefore(date: String, limit: Int): List<DailyLogSummaryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(summary: DailyLogSummaryEntity)

    @Query("DELETE FROM daily_log_summaries WHERE date = :date")
    suspend fun deleteForDate(date: String)
}
