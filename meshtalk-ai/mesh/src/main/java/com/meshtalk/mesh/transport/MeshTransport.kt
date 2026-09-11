package com.meshtalk.mesh.transport

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.Transport
import kotlinx.coroutines.flow.Flow

/**
 * Every physical radio the mesh can use implements this same surface, so
 * [com.meshtalk.mesh.MeshManager] can select and hot-swap between them without
 * caring about the underlying Android API (WifiP2pManager, WifiAwareManager,
 * BluetoothLeAdvertiser/Scanner, or a future UWB ranging API).
 *
 * Contract: implementations own their own permission checks and must fail closed
 * (emit [MeshResult.Failure] / close the neighbor flow) rather than throw across
 * this boundary - MeshManager treats every transport as equally unreliable.
 */
interface MeshTransport {
    val type: Transport

    /** True if the radio/API is present and permissions are granted on this device. */
    suspend fun isAvailable(): Boolean

    suspend fun startAdvertising(): MeshResult<Unit>
    suspend fun stopAdvertising()

    suspend fun startDiscovery(): MeshResult<Unit>
    suspend fun stopDiscovery()

    /** Directly-reachable peers seen by this transport, updated as neighbors churn. */
    fun observeDiscoveredPeers(): Flow<List<DiscoveredPeer>>

    /** Sends one already-encrypted frame directly to a one-hop neighbor. Routing/multi-hop
     *  logic lives entirely in [com.meshtalk.mesh.routing.RoutingEngine], not here - a
     *  transport only ever knows about its immediate neighbors. */
    suspend fun sendFrame(neighborDeviceId: String, frame: ByteArray): MeshResult<Unit>

    /** Frames received from any neighbor, still opaque bytes - the mesh/routing layer
     *  is responsible for parsing headers and deciding whether to relay or hand off
     *  to :crypto for decryption. */
    fun observeIncomingFrames(): Flow<IncomingFrame>
}

data class DiscoveredPeer(
    val deviceId: String,
    val transport: Transport,
    val rssi: Int?,
    val lastSeenEpochMillis: Long
)

data class IncomingFrame(
    val fromDeviceId: String,
    val transport: Transport,
    val bytes: ByteArray
)
