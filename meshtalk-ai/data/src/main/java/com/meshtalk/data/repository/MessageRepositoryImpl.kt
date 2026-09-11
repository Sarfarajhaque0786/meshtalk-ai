package com.meshtalk.data.repository

import com.meshtalk.core.common.util.MeshError
import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.core.database.dao.MessageDao
import com.meshtalk.core.database.entity.MessageEntity
import com.meshtalk.domain.model.DeliveryStatus
import com.meshtalk.domain.model.Message
import com.meshtalk.domain.model.MessageContent
import com.meshtalk.domain.repository.CryptoRepository
import com.meshtalk.domain.repository.MessageRepository
import com.meshtalk.mesh.MeshManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Composition point for the whole "send a message" pipeline:
 *   domain MessageContent -> serialize -> :crypto encrypt -> MessageEntity (ciphertext
 *   only) -> persist via MessageDao -> :mesh MeshManager.sendFrame to next hop.
 *
 * Content serialization here is intentionally minimal (UTF-8 text only) - wiring up
 * the full MessageContent sealed hierarchy (Media/Location/EmergencyBroadcast) through
 * a real wire format (protobuf, matching the spec's "Binary serialization" requirement)
 * is a follow-up once the crypto layer's encrypt/decrypt is actually implemented,
 * since there's no point designing the wire format against stub crypto.
 */
@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val cryptoRepository: CryptoRepository,
    private val meshManager: MeshManager
) : MessageRepository {

    override fun observeConversation(conversationId: String): Flow<List<Message>> =
        messageDao.observeConversation(conversationId).map { entities ->
            entities.mapNotNull { entity -> decryptEntityOrNull(entity) }
        }

    override suspend fun send(
        conversationId: String,
        recipientId: String,
        content: MessageContent,
        expiresAt: Long?
    ): MeshResult<Message> {
        val plaintext = serialize(content) ?: return MeshResult.Failure(
            MeshError.Unknown(NotImplementedError("Serialization for $content not yet implemented"))
        )

        val envelope = when (val result = cryptoRepository.encryptForPeer(recipientId, plaintext)) {
            is MeshResult.Success -> result.data
            is MeshResult.Failure -> return result
        }

        val now = System.currentTimeMillis()
        val entity = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = LOCAL_DEVICE_PLACEHOLDER_ID,
            ciphertext = envelope.ciphertext,
            nonce = envelope.nonce,
            ratchetHeader = envelope.ratchetHeader,
            sentAtEpochMillis = now,
            receivedAtEpochMillis = null,
            deliveryState = DeliveryStatus.QUEUED.name,
            hopCount = 0,
            expiresAtEpochMillis = expiresAt,
            isBroadcast = false
        )
        messageDao.insert(entity)

        val frame = envelope.ciphertext // TODO: wrap with routing header (dest, ttl, msg id) before sending
        val sendResult = meshManager.sendFrame(recipientId, frame)
        if (sendResult is MeshResult.Failure) {
            // Left QUEUED in the DB - retryQueued()/WorkManager will retry once a route appears.
            return MeshResult.Success(entity.toDomain(content))
        }

        messageDao.update(entity.copy(deliveryState = DeliveryStatus.RELAYING.name))
        return MeshResult.Success(entity.toDomain(content).copy(deliveryStatus = DeliveryStatus.RELAYING))
    }

    override suspend fun sendBroadcast(content: MessageContent): MeshResult<Unit> {
        val plaintext = serialize(content) ?: return MeshResult.Failure(
            MeshError.Unknown(NotImplementedError("Serialization for $content not yet implemented"))
        )
        // Broadcasts are unencrypted-per-recipient by nature (no single recipient key to
        // target) - relies on transport-layer + future group-key handling. Flooded as-is
        // for now; see MeshManager.floodFrame TODO for the real fan-out + dedupe logic.
        return meshManager.floodFrame(plaintext)
    }

    override suspend fun onMessageReceived(message: Message): MeshResult<Unit> {
        // Entity was already persisted by whatever decrypted it (mesh ingress pipeline);
        // this hook exists for post-receipt side effects: notifications, AI priority
        // classification, read-receipt scheduling.
        return MeshResult.Success(Unit)
    }

    override suspend fun retryQueued(): MeshResult<Int> {
        val queued = messageDao.getQueuedForRetry()
        var succeeded = 0
        queued.forEach { entity ->
            val result = meshManager.sendFrame(entity.conversationId, entity.ciphertext)
            if (result is MeshResult.Success) {
                messageDao.update(entity.copy(deliveryState = DeliveryStatus.RELAYING.name))
                succeeded++
            }
        }
        return MeshResult.Success(succeeded)
    }

    override suspend fun purgeExpired(): MeshResult<Int> =
        MeshResult.Success(messageDao.purgeExpired(System.currentTimeMillis()))

    private suspend fun decryptEntityOrNull(entity: MessageEntity): Message? {
        val envelope = com.meshtalk.domain.repository.EncryptedEnvelope(
            ciphertext = entity.ciphertext,
            nonce = entity.nonce,
            ratchetHeader = entity.ratchetHeader
        )
        val plaintext = when (val result = cryptoRepository.decryptFromPeer(entity.senderId, envelope)) {
            is MeshResult.Success -> result.data
            is MeshResult.Failure -> return null // undecryptable message hidden from UI, not crashed on
        }
        val content = deserialize(plaintext) ?: return null
        return entity.toDomain(content)
    }

    private fun serialize(content: MessageContent): ByteArray? = when (content) {
        is MessageContent.Text -> content.body.toByteArray(Charsets.UTF_8)
        else -> null // TODO: Media/Location/EmergencyBroadcast wire format
    }

    private fun deserialize(bytes: ByteArray): MessageContent? =
        MessageContent.Text(String(bytes, Charsets.UTF_8))

    private companion object {
        // TODO: replace with IdentityManager.getOrCreateIdentityKeyPair()-derived device id
        const val LOCAL_DEVICE_PLACEHOLDER_ID = "local-device"
    }
}
