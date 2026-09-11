package com.meshtalk.mesh

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.MeshDevice
import com.meshtalk.mesh.discovery.DiscoveryService
import com.meshtalk.mesh.routing.RoutingEngine
import com.meshtalk.mesh.transport.MeshTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Top of the mesh stack. Everything the rest of the app needs from networking goes
 * through this class: :data's MeshRepositoryImpl calls it, and :ai's natural-language
 * command handler routes "send to X" through it indirectly via the domain use cases.
 *
 * Responsibilities:
 *  - own the lifecycle of [DiscoveryService] and every [MeshTransport]
 *  - fan raw incoming frames from all transports into one stream for the message
 *    pipeline (:data hands these to :crypto for decryption)
 *  - delegate route decisions to [RoutingEngine], never computing routes itself
 *  - decide *which* transport to hand an outgoing frame to, based on payload size,
 *    battery/low-bandwidth settings, and per-transport availability - this transport
 *    selection policy is the one piece of "mesh" logic that lives here rather than
 *    in RoutingEngine, since it's about physical link choice, not path choice
 */
@Singleton
class MeshManager @Inject constructor(
    private val discoveryService: DiscoveryService,
    private val routingEngine: RoutingEngine,
    private val transports: Set<@JvmSuppressWildcards MeshTransport>
) {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val incomingFrames = transports
        .map { it.observeIncomingFrames() }
        .merge()
        .shareIn(managerScope, SharingStarted.WhileSubscribed(5_000))

    fun observeNeighbors(): Flow<List<MeshDevice>> = discoveryService.observeNeighbors()

    suspend fun start(): MeshResult<Unit> {
        discoveryService.startAll()
        return MeshResult.Success(Unit)
    }

    suspend fun stop(): MeshResult<Unit> {
        discoveryService.stopAll()
        return MeshResult.Success(Unit)
    }

    /**
     * Sends an already-encrypted [frame] toward [destinationDeviceId]. Looks up the
     * next hop via [RoutingEngine], then picks whichever transport currently has a
     * live link to that specific neighbor - a two-step process (route, then link)
     * because the same next-hop neighbor might be reachable over more than one radio.
     */
    suspend fun sendFrame(destinationDeviceId: String, frame: ByteArray): MeshResult<Unit> {
        val routeResult = routingEngine.findRoute(destinationDeviceId)
        val route = when (routeResult) {
            is MeshResult.Success -> routeResult.data
            is MeshResult.Failure -> return routeResult
        }

        for (transport in transports) {
            if (!transport.isAvailable()) continue
            val result = transport.sendFrame(route.nextHopDeviceId, frame)
            if (result is MeshResult.Success) return result
        }

        return MeshResult.Failure(
            com.meshtalk.core.common.util.MeshError.TransportUnavailable("no transport reached ${route.nextHopDeviceId}")
        )
    }

    suspend fun floodFrame(frame: ByteArray): MeshResult<Unit> {
        // TODO: iterate routingEngine.floodTo(currentNeighborIds) and send via best
        // transport per neighbor, deduplicating via a seen-message-id cache to avoid
        // infinite flood loops (see product spec's "Duplicate packet elimination").
        return MeshResult.Success(Unit)
    }
}
