package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Chat Message Entity
 * 
 * Stores chat-style logging messages.
 * Supports message types per PRD: user text, user image, system draft card, assistant clarifying prompts.
 */
@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["date"]),
        Index(value = ["timestamp"])
    ]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Date of message (ISO date: "YYYY-MM-DD")
     */
    val date: String,
    
    /**
     * Timestamp (epoch millis)
     */
    val timestamp: Long,
    
    /**
     * Message type:
     * - "user_text": User's text input
     * - "user_image": User's photo input
     * - "system_draft": Draft card for confirmation
     * - "assistant_clarify": Clarifying question from assistant
     * - "system_confirmed": Confirmed entry (linked to food/exercise entry)
     */
    val messageType: String,
    
    /**
     * Role: "user", "assistant", "system"
     */
    val role: String,
    
    /**
     * Text content of the message
     */
    val content: String?,
    
    /**
     * Path to image (for user_image type)
     */
    val imagePath: String?,
    
    /**
     * JSON data for draft/confirmed entries
     */
    val entryDataJson: String?,
    
    /**
     * Draft status: "drafting", "needs_confirmation", "confirmed", "cancelled"
     */
    val draftStatus: String?,
    
    /**
     * Linked food entry ID (after confirmation)
     */
    val linkedFoodEntryId: Long?,
    
    /**
     * Linked exercise entry ID (after confirmation)
     */
    val linkedExerciseEntryId: Long?,
    
    /**
     * Message creation timestamp (epoch millis)
     */
    val createdAt: Long
)

/**
 * Message type constants
 */
object ChatMessageType {
    const val USER_TEXT = "user_text"
    const val USER_IMAGE = "user_image"
    const val SYSTEM_DRAFT = "system_draft"
    const val ASSISTANT_CLARIFY = "assistant_clarify"
    const val SYSTEM_CONFIRMED = "system_confirmed"
}

/**
 * Draft status constants
 */
object DraftStatus {
    const val DRAFTING = "drafting"
    const val NEEDS_CONFIRMATION = "needs_confirmation"
    const val CONFIRMED = "confirmed"
    const val CANCELLED = "cancelled"
}

/**
 * Message role constants
 */
object MessageRole {
    const val USER = "user"
    const val ASSISTANT = "assistant"
    const val SYSTEM = "system"
}
