package com.tracky.app.domain.model

/**
 * Domain model for Chat Message
 */
data class ChatMessage(
    val id: Long = 0,
    val date: String,
    val timestamp: Long,
    val messageType: ChatMessageType,
    val role: MessageRole,
    val content: String?,
    val imagePath: String?,
    val draftData: DraftData?,
    val draftStatus: DraftStatus?,
    val linkedFoodEntryId: Long?,
    val linkedExerciseEntryId: Long?,
    val createdAt: Long
)

/**
 * Message types per PRD
 */
enum class ChatMessageType(val value: String) {
    USER_TEXT("user_text"),
    USER_IMAGE("user_image"),
    SYSTEM_DRAFT("system_draft"),
    ASSISTANT_CLARIFY("assistant_clarify"),
    SYSTEM_CONFIRMED("system_confirmed");

    companion object {
        fun fromValue(value: String): ChatMessageType {
            return entries.find { it.value == value } ?: USER_TEXT
        }
    }
}

/**
 * Message roles
 */
enum class MessageRole(val value: String) {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    companion object {
        fun fromValue(value: String): MessageRole {
            return entries.find { it.value == value } ?: USER
        }
    }
}

/**
 * Draft lifecycle status per PRD:
 * Drafting -> NeedsConfirmation -> Confirmed -> Editable
 */
enum class DraftStatus(val value: String) {
    DRAFTING("drafting"),
    NEEDS_CONFIRMATION("needs_confirmation"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled");

    companion object {
        fun fromValue(value: String?): DraftStatus? {
            return value?.let { v -> entries.find { it.value == v } }
        }
    }
}

/**
 * Data structure for draft entries
 */

