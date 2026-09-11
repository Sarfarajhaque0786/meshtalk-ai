package com.meshtalk.domain.usecase

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.Message
import com.meshtalk.domain.model.MessageContent
import com.meshtalk.domain.repository.MessageRepository
import javax.inject.Inject

/**
 * A use case per user-facing intent (per Clean Architecture), rather than a fat
 * "MessageInteractor" - keeps each operation independently testable and gives the
 * AI Service's natural-language command handler ("Send location to Rahul") a single
 * well-defined entry point to invoke, same as the chat UI does.
 */
class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        recipientId: String,
        content: MessageContent,
        expiresAt: Long? = null
    ): MeshResult<Message> = messageRepository.send(conversationId, recipientId, content, expiresAt)
}
