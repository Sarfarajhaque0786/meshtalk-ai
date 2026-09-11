package com.meshtalk.data.repository

import com.meshtalk.core.database.entity.MessageEntity
import com.meshtalk.domain.model.DeliveryStatus
import com.meshtalk.domain.model.Message
import com.meshtalk.domain.model.MessageContent

/**
 * MessageEntity only ever stores ciphertext; converting it into a domain [Message]
 * requires a decrypt step this mapper deliberately does NOT perform - that belongs
 * in the repository, which has access to CryptoRepository. This file only maps the
 * envelope-level fields (ids, timestamps, delivery state) and the *already
 * decrypted* plaintext bytes passed in alongside the entity.
 */
fun MessageEntity.toDomain(decryptedContent: MessageContent): Message = Message(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    content = decryptedContent,
    sentAt = sentAtEpochMillis,
    receivedAt = receivedAtEpochMillis,
    deliveryStatus = DeliveryStatus.valueOf(deliveryState),
    hopCount = hopCount,
    expiresAt = expiresAtEpochMillis,
    isBroadcast = isBroadcast
)
