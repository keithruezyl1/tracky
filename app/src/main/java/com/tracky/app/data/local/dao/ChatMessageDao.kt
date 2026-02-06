package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tracky.app.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    
    /**
     * Get messages for a specific date
     */
    @Query("SELECT * FROM chat_messages WHERE date = :date ORDER BY timestamp ASC")
    fun getMessagesForDate(date: String): Flow<List<ChatMessageEntity>>
    
    /**
     * Get messages for date one-shot
     */
    @Query("SELECT * FROM chat_messages WHERE date = :date ORDER BY timestamp ASC")
    suspend fun getMessagesForDateOnce(date: String): List<ChatMessageEntity>
    
    /**
     * Get recent messages across all dates
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int = 50): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getMessageById(id: Long): ChatMessageEntity?
    
    /**
     * Get pending draft messages (needs confirmation)
     */
    @Query("""
        SELECT * FROM chat_messages 
        WHERE draftStatus = 'needs_confirmation' 
        ORDER BY timestamp DESC
    """)
    fun getPendingDrafts(): Flow<List<ChatMessageEntity>>
    
    @Insert
    suspend fun insert(message: ChatMessageEntity): Long
    
    @Update
    suspend fun update(message: ChatMessageEntity)
    
    @Delete
    suspend fun delete(message: ChatMessageEntity)
    
    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    /**
     * Get the most recent user message for a date
     */
    @Query("""
        SELECT * FROM chat_messages 
        WHERE date = :date AND messageType = 'user_text' 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getMostRecentUserMessage(date: String): ChatMessageEntity?
    
    /**
     * Update draft status
     */
    @Query("UPDATE chat_messages SET draftStatus = :status WHERE id = :id")
    suspend fun updateDraftStatus(id: Long, status: String)
    
    /**
     * Link confirmed entry
     */
    @Query("""
        UPDATE chat_messages 
        SET draftStatus = 'confirmed', 
            linkedFoodEntryId = :foodEntryId, 
            linkedExerciseEntryId = :exerciseEntryId 
        WHERE id = :messageId
    """)
    suspend fun linkConfirmedEntry(
        messageId: Long, 
        foodEntryId: Long?, 
        exerciseEntryId: Long?
    )
}
