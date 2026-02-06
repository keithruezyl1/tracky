package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.ChatMessageDao
import com.tracky.app.data.local.entity.ChatMessageEntity
import com.tracky.app.data.local.entity.ChatMessageType as EntityChatMessageType
import com.tracky.app.data.local.entity.DraftStatus as EntityDraftStatus
import com.tracky.app.data.local.entity.MessageRole as EntityMessageRole
import com.tracky.app.data.mapper.toDomain
import com.tracky.app.domain.model.ChatMessage
import com.tracky.app.domain.model.ChatMessageType
import com.tracky.app.domain.model.DraftStatus
import com.tracky.app.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for chat messages backing the visible chat thread UI.
 */
@Singleton
class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) {

    /**
     * Stream chat messages for a specific date (ISO "YYYY-MM-DD"), ordered by timestamp ASC.
     */
    fun getMessagesForDate(date: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Append a user text message to the chat thread.
     */
    suspend fun addUserTextMessage(date: String, text: String) {
        val now = System.currentTimeMillis()
        val entity = ChatMessageEntity(
            id = 0,
            date = date,
            timestamp = now,
            messageType = EntityChatMessageType.USER_TEXT,
            role = EntityMessageRole.USER,
            content = text,
            imagePath = null,
            entryDataJson = null,
            draftStatus = null,
            linkedFoodEntryId = null,
            linkedExerciseEntryId = null,
            createdAt = now
        )
        chatMessageDao.insert(entity)
    }

    /**
     * Append a system message after an entry has been confirmed.
     */
    suspend fun addSystemConfirmedMessage(
        date: String,
        content: String,
        foodEntryId: Long? = null,
        exerciseEntryId: Long? = null
    ) {
        val now = System.currentTimeMillis()
        val entity = ChatMessageEntity(
            id = 0,
            date = date,
            timestamp = now,
            messageType = EntityChatMessageType.SYSTEM_CONFIRMED,
            role = EntityMessageRole.SYSTEM,
            content = content,
            imagePath = null,
            entryDataJson = null,
            draftStatus = EntityDraftStatus.CONFIRMED,
            linkedFoodEntryId = foodEntryId,
            linkedExerciseEntryId = exerciseEntryId,
            createdAt = now
        )
        chatMessageDao.insert(entity)
    }

    /**
     * Delete a chat message by its ID
     */
    suspend fun deleteMessage(messageId: Long) {
        chatMessageDao.deleteById(messageId)
    }

    /**
     * Delete the most recent user message for a date (used after confirmation)
     */
    suspend fun deleteMostRecentUserMessage(date: String) {
        val message = chatMessageDao.getMostRecentUserMessage(date)
        message?.let {
            chatMessageDao.deleteById(it.id)
        }
    }
}

