package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tracky.app.data.local.entity.SavedEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedEntryDao {
    
    /**
     * Get all saved entries ordered by use count and last used
     */
    @Query("""
        SELECT * FROM saved_entries 
        ORDER BY useCount DESC, (lastUsedAt IS NULL) ASC, lastUsedAt DESC
    """)
    fun getAllEntries(): Flow<List<SavedEntryEntity>>
    
    /**
     * Get saved entries by type
     */
    @Query("""
        SELECT * FROM saved_entries 
        WHERE entryType = :type 
        ORDER BY useCount DESC, (lastUsedAt IS NULL) ASC, lastUsedAt DESC
    """)
    fun getEntriesByType(type: String): Flow<List<SavedEntryEntity>>
    
    /**
     * Search saved entries by name
     */
    @Query("""
        SELECT * FROM saved_entries 
        WHERE name LIKE '%' || :query || '%' 
        ORDER BY useCount DESC
    """)
    fun searchEntries(query: String): Flow<List<SavedEntryEntity>>
    
    @Query("SELECT * FROM saved_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): SavedEntryEntity?
    
    @Insert
    suspend fun insert(entry: SavedEntryEntity): Long
    
    @Update
    suspend fun update(entry: SavedEntryEntity)
    
    @Delete
    suspend fun delete(entry: SavedEntryEntity)
    
    @Query("DELETE FROM saved_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    /**
     * Increment use count and update last used timestamp
     */
    @Query("""
        UPDATE saved_entries 
        SET useCount = useCount + 1, lastUsedAt = :timestamp, updatedAt = :timestamp 
        WHERE id = :id
    """)
    suspend fun incrementUseCount(id: Long, timestamp: Long)
}
