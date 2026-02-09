package com.tracky.app.domain.usecase

import com.tracky.app.data.local.dao.DailyLogSummaryDao
import com.tracky.app.data.local.preferences.UserPreferencesDataStore
import com.tracky.app.domain.model.StreakInfo
import com.tracky.app.domain.model.StreakStatus
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakInteractor @Inject constructor(
    private val dailyLogSummaryDao: DailyLogSummaryDao,
    private val prefs: UserPreferencesDataStore
) {
    /**
     * Recomputes the streak by walking back from today.
     * Uses incremental logic: only scans until a streak break is found.
     */
    suspend fun calculateStreak(): StreakInfo {
        val timezoneStr = prefs.homeTimezone.first() ?: TimeZone.currentSystemDefault().id
        val timezone = try { TimeZone.of(timezoneStr) } catch (e: Exception) { TimeZone.currentSystemDefault() }
        val today = Clock.System.now().toLocalDateTime(timezone).date
        
        var currentCount = 0
        var streakBroken = false
        var lastGraceUsedDate: LocalDate? = prefs.lastGraceDate.first()?.let { 
            try { LocalDate.parse(it) } catch (e: Exception) { null }
        }
        
        // 1. Check Today
        val todaySummary = dailyLogSummaryDao.getSummaryForDate(today.toString())
        val isTodayQualifying = (todaySummary?.qualifyingEntriesCount ?: 0) >= 1
        
        // 2. Walk back from Yesterday
        var checkDate = today.minus(1, DateTimeUnit.DAY)
        var consecutiveDays = 0
        
        // We limit stay-sane walkback to 365 days
        for (i in 1..365) {
            val summary = dailyLogSummaryDao.getSummaryForDate(checkDate.toString())
            val isQualifying = (summary?.qualifyingEntriesCount ?: 0) >= 1
            
            if (isQualifying) {
                consecutiveDays++
            } else {
                // Check if we can use a Grace Day
                // Limit: 1 grace per 7 days
                val canUseGrace = lastGraceUsedDate == null || 
                                 (checkDate.toEpochDays() - lastGraceUsedDate.toEpochDays()) > 7
                
                if (canUseGrace) {
                    // Use grace for this day
                    lastGraceUsedDate = checkDate
                    // Streak continues but count doesn't increment for this day?
                    // PRD: "streak continues without increment"
                } else {
                    streakBroken = true
                    break
                }
            }
            checkDate = checkDate.minus(1, DateTimeUnit.DAY)
        }
        
        currentCount = consecutiveDays + (if (isTodayQualifying) 1 else 0)
        
        val status = when {
            isTodayQualifying -> StreakStatus.ACTIVE
            consecutiveDays > 0 || (lastGraceUsedDate != null && lastGraceUsedDate == today.minus(1, DateTimeUnit.DAY)) -> {
                 // Check if yesterday was active or grace
                 StreakStatus.AT_RISK
            }
            else -> StreakStatus.NONE
        }

        // Special case for FROZEN: if yesterday was a grace day
        val finalStatus = if (status == StreakStatus.AT_RISK && lastGraceUsedDate == today.minus(1, DateTimeUnit.DAY)) {
            StreakStatus.FROZEN
        } else status

        val info = StreakInfo(
            count = currentCount,
            status = finalStatus,
            lastGraceDate = lastGraceUsedDate?.toString(),
            isPerfectDay = todaySummary?.metGoal ?: false
        )
        
        // Persist
        prefs.setStreakStateJson(Json.encodeToString(StreakInfo.serializer(), info))
        lastGraceUsedDate?.let { prefs.setLastGraceDate(it.toString()) }
        
        return info
    }

    suspend fun getStreakInfo(): StreakInfo {
        val json = prefs.streakStateJson.first()
        return if (json != null) {
            try {
                Json.decodeFromString(StreakInfo.serializer(), json)
            } catch (e: Exception) {
                calculateStreak()
            }
        } else {
            calculateStreak()
        }
    }
}
