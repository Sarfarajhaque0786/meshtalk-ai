package com.meshtalk.domain.model

/**
 * Plaintext, in-memory representation of a message - the result of decrypting a
 * MessageEntity. Never persisted directly; the presentation layer holds it only as
 * long as the Compose screen showing it is alive.
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: MessageContent,
    val sentAt: Long,
    val receivedAt: Long?,
    val deliveryStatus: DeliveryStatus,
    val hopCount: Int,
    val expiresAt: Long?,
    val isBroadcast: Boolean
)

sealed interface MessageContent {
    data class Text(val body: String) : MessageContent
    data class Media(val uri: String, val mimeType: String, val sizeBytes: Long) : MessageContent
    data class Location(val latitude: Double, val longitude: Double, val label: String?) : MessageContent
    data class EmergencyBroadcast(val body: String, val category: EmergencyCategory) : MessageContent
}

enum class EmergencyCategory { SOS, MEDICAL, DISASTER, GENERAL }

enum class DeliveryStatus { QUEUED, RELAYING, DELIVERED, READ, FAILED }
