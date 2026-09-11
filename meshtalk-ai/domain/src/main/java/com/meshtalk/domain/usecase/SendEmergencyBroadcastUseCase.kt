package com.meshtalk.domain.usecase

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.EmergencyCategory
import com.meshtalk.domain.model.MessageContent
import com.meshtalk.domain.repository.MeshRepository
import com.meshtalk.domain.repository.MessageRepository
import javax.inject.Inject

/**
 * Emergency broadcasts bypass normal per-conversation routing: they flood-route to
 * every reachable node (see RoutingEngine's FLOOD mode) and are exempt from the
 * user's configured low-bandwidth throttling. This use case encodes that priority
 * behavior at the domain layer so both the manual SOS button and the AI Service's
 * "create emergency broadcast" voice command share one code path.
 */
class SendEmergencyBroadcastUseCase @Inject constructor(
    private val meshRepository: MeshRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(body: String, category: EmergencyCategory): MeshResult<Unit> {
        // Ensure we're not sitting idle before flooding an SOS.
        meshRepository.startMesh()
        return messageRepository.sendBroadcast(MessageContent.EmergencyBroadcast(body, category))
    }
}
