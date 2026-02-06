package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracky.app.data.local.entity.DailyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    
    /**
     * Get the current goal (latest by effectiveFromDate)
     */
    @Query("""
        SELECT * FROM daily_goals 
        WHERE effectiveFromDate <= :date 
        ORDER BY effectiveFromDate DESC 
        LIMIT 1
    """)
    fun getCurrentGoal(date: String): Flow<DailyGoalEntity?>
    
    @Query("""
        SELECT * FROM daily_goals 
        WHERE effectiveFromDate <= :date 
        ORDER BY effectiveFromDate DESC 
        LIMIT 1
    """)
    suspend fun getCurrentGoalOnce(date: String): DailyGoalEntity?
    
    /**
     * Get goal for a specific date
     */
    @Query("SELECT * FROM daily_goals WHERE effectiveFromDate = :date")
    suspend fun getGoalForDate(date: String): DailyGoalEntity?
    
    /**
     * Get all goals history
     */
    @Query("SELECT * FROM daily_goals ORDER BY effectiveFromDate DESC")
    fun getAllGoals(): Flow<List<DailyGoalEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: DailyGoalEntity): Long
    
    @Update
    suspend fun update(goal: DailyGoalEntity)
    
    @Query("DELETE FROM daily_goals WHERE id = :id")
    suspend fun delete(id: Long)
    
    @Query("SELECT EXISTS(SELECT 1 FROM daily_goals)")
    suspend fun hasAnyGoals(): Boolean
}
