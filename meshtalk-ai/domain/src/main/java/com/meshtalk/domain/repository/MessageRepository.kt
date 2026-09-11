package com.meshtalk.domain.repository

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.Message
import com.meshtalk.domain.model.MessageContent
import kotlinx.coroutines.flow.Flow

/**
 * Implemented in :data. Bridges the domain layer to :core:database (persistence),
 * :crypto (encrypt/decrypt) and :mesh (actually transmitting to the next hop).
 */
interface MessageRepository {
    fun observeConversation(conversationId: String): Flow<List<Message>>

    suspend fun send(
        conversationId: String,
        recipientId: String,
        content: MessageContent,
        expiresAt: Long? = null
    ): MeshResult<Message>

    suspend fun sendBroadcast(content: MessageContent): MeshResult<Unit>

    /** Called by the mesh layer when a message addressed to this device is decrypted. */
    suspend fun onMessageReceived(message: Message): MeshResult<Unit>

    suspend fun retryQueued(): MeshResult<Int>

    suspend fun purgeExpired(): MeshResult<Int>
}
