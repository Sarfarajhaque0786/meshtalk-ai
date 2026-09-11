package com.meshtalk.mesh.discovery

import com.meshtalk.domain.model.MeshDevice
import com.meshtalk.mesh.transport.MeshTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Merges the [MeshTransport.observeDiscoveredPeers] flows from every registered
 * transport into a single deduplicated neighbor list, preferring whichever
 * transport currently reports the strongest link for a given device. MeshManager
 * consumes this rather than talking to individual transports directly.
 */
@Singleton
class DiscoveryService @Inject constructor(
    private val transports: Set<@JvmSuppressWildcards MeshTransport>
) {
    fun observeNeighbors(): Flow<List<MeshDevice>> {
        if (transports.isEmpty()) return kotlinx.coroutines.flow.flowOf(emptyList())

        val perTransportFlows = transports.map { transport ->
            transport.observeDiscoveredPeers().map { peers ->
                peers.map { peer ->
                    MeshDevice(
                        deviceId = peer.deviceId,
                        ownerContactId = null, // resolved later by :data joining against UserDao
                        transport = peer.transport,
                        rssi = peer.rssi,
                        reputationScore = 0f, // resolved later by :data joining against DeviceDao
                        isDirectNeighbor = true,
                        lastSeen = peer.lastSeenEpochMillis
                    )
                }
            }
        }

        return combine(perTransportFlows) { lists ->
            lists.flatMap { it }
                .groupBy { it.deviceId }
                .map { (_, candidates) -> candidates.maxBy { it.rssi ?: Int.MIN_VALUE } }
        }
    }

    suspend fun startAll() {
        transports.forEach { transport ->
            if (transport.isAvailable()) {
                transport.startAdvertising()
                transport.startDiscovery()
            }
        }
    }

    suspend fun stopAll() {
        transports.forEach { transport ->
            transport.stopAdvertising()
            transport.stopDiscovery()
        }
    }
}
