package com.meshtalk.mesh.transport

import android.content.Context
import com.meshtalk.core.common.util.MeshError
import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.Transport
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wi-Fi Direct (WifiP2pManager) transport. Chosen by MeshManager as the primary
 * transport when higher throughput is needed (media, large file transfer) and the
 * radio is available - it supports larger payloads and better range than BLE at the
 * cost of higher battery draw and a slower (~seconds) connection setup.
 *
 * NOT wired to the real WifiP2pManager APIs yet in this skeleton - group formation,
 * the WifiP2pManager.ChannelListener/PeerListListener callback bridging into these
 * Flows, and socket-based framing over the resulting P2P group are the concrete next
 * step. The interface below is the real contract MeshManager will code against, so
 * that implementation can land without touching any other module.
 */
@Singleton
class WifiDirectTransport @Inject constructor(
    @ApplicationContext private val context: Context
) : MeshTransport {

    override val type: Transport = Transport.WIFI_DIRECT

    private val discoveredPeers = MutableStateFlow<List<DiscoveredPeer>>(emptyList())
    private val incomingFrames = MutableSharedFlow<IncomingFrame>(extraBufferCapacity = 64)

    override suspend fun isAvailable(): Boolean {
        // TODO: check PackageManager.FEATURE_WIFI_DIRECT + WifiP2pManager availability
        // and BLUETOOTH_CONNECT/NEARBY_WIFI_DEVICES runtime permission grants.
        return false
    }

    override suspend fun startAdvertising(): MeshResult<Unit> =
        MeshResult.Failure(MeshError.TransportUnavailable("WIFI_DIRECT"))

    override suspend fun stopAdvertising() = Unit

    override suspend fun startDiscovery(): MeshResult<Unit> =
        MeshResult.Failure(MeshError.TransportUnavailable("WIFI_DIRECT"))

    override suspend fun stopDiscovery() = Unit

    override fun observeDiscoveredPeers(): Flow<List<DiscoveredPeer>> = discoveredPeers.asStateFlow()

    override suspend fun sendFrame(neighborDeviceId: String, frame: ByteArray): MeshResult<Unit> =
        MeshResult.Failure(MeshError.TransportUnavailable("WIFI_DIRECT"))

    override fun observeIncomingFrames(): Flow<IncomingFrame> = incomingFrames.asSharedFlow()
}
