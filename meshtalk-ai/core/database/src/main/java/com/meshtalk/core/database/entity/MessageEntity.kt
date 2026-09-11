package com.meshtalk.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * IMPORTANT: [ciphertext] is the only payload column. Plaintext never touches disk -
 * decryption happens in-memory in the :crypto module and results are handed to the
 * UI layer as transient domain models, never persisted. This table survives even a
 * full device compromise without exposing message content (subject to key storage
 * integrity - see SecurityManager / Android Keystore usage in :crypto).
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val ciphertext: ByteArray,
    val nonce: ByteArray,
    val ratchetHeader: ByteArray,
    val sentAtEpochMillis: Long,
    val receivedAtEpochMillis: Long?,
    val deliveryState: String, // QUEUED | RELAYING | DELIVERED | READ | FAILED
    val hopCount: Int,
    val expiresAtEpochMillis: Long?, // disappearing messages / self-destruct timer
    val isBroadcast: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
