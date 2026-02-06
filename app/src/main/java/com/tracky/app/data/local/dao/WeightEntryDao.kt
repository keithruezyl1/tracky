package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracky.app.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    
    /**
     * Get all weight entries ordered by date descending
     */
    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<WeightEntryEntity>>
    
    /**
     * Get entries for a date range (for chart)
     */
    @Query("""
        SELECT * FROM weight_entries 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY timestamp ASC
    """)
    fun getEntriesInRange(startDate: String, endDate: String): Flow<List<WeightEntryEntity>>
    
    /**
     * Get entries for a date range (one-shot)
     */
    @Query("""
        SELECT * FROM weight_entries 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY timestamp ASC
    """)
    suspend fun getEntriesInRangeOnce(startDate: String, endDate: String): List<WeightEntryEntity>
    
    /**
     * Get the latest weight entry
     */
    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC LIMIT 1")
    fun getLatestEntry(): Flow<WeightEntryEntity?>
    
    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEntryOnce(): WeightEntryEntity?
    
    /**
     * Get entry for a specific date
     */
    @Query("SELECT * FROM weight_entries WHERE date = :date")
    suspend fun getEntryForDate(date: String): WeightEntryEntity?
    
    @Query("SELECT * FROM weight_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): WeightEntryEntity?
    
    @Insert
    suspend fun insert(entry: WeightEntryEntity): Long
    
    @Update
    suspend fun update(entry: WeightEntryEntity)
    
    @Delete
    suspend fun delete(entry: WeightEntryEntity)
    
    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    /**
     * Get count of entries
     */
    @Query("SELECT COUNT(*) FROM weight_entries")
    suspend fun getEntryCount(): Int
}
