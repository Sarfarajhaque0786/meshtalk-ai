package com.meshtalk.domain.usecase

import com.meshtalk.domain.model.Message
import com.meshtalk.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConversationUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: String): Flow<List<Message>> =
        messageRepository.observeConversation(conversationId)
}
