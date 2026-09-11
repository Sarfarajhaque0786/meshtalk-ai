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
 * Bluetooth LE transport: lowest power, lowest throughput, best baseline range/
 * reliability of the options available on stock Android. MeshManager treats this as
 * the fallback-of-last-resort transport for control traffic (routing updates, small
 * text messages, emergency beacons) when Wi-Fi Direct/Aware aren't available or
 * would cost too much battery, per the low-bandwidth/battery-saver settings in
 * :core:datastore.
 *
 * Real implementation would advertise/scan via BluetoothLeAdvertiser and
 * BluetoothLeScanner with a MeshTalk-specific service UUID, and frame messages
 * across GATT writes (payload-size-limited, hence "control traffic" framing above -
 * anything larger gets handed to Wi-Fi Direct instead once a route is confirmed).
 */
@Singleton
class BleTransport @Inject constructor(
    @ApplicationContext private val context: Context
) : MeshTransport {

    override val type: Transport = Transport.BLUETOOTH_LE

    private val discoveredPeers = MutableStateFlow<List<DiscoveredPeer>>(emptyList())
    private val incomingFrames = MutableSharedFlow<IncomingFrame>(extraBufferCapacity = 64)

    override suspend fun isAvailable(): Boolean {
        // TODO: check BluetoothAdapter.isMultipleAdvertisementSupported() and
        // BLUETOOTH_SCAN/BLUETOOTH_ADVERTISE/BLUETOOTH_CONNECT runtime grants.
        return false
    }

    override suspend fun startAdvertising(): MeshResult<Unit> =
        MeshResult.Failure(MeshError.TransportUnavailable("BLUETOOTH_LE"))

    override suspend fun stopAdvertising() = Unit

    override suspend fun startDiscovery(): MeshResult<Unit> =
        MeshResult.Failure(MeshError.TransportUnavailable("BLUETOOTH_LE"))

    override suspend fun stopDiscovery() = Unit

    override fun observeDiscoveredPeers(): Flow<List<DiscoveredPeer>> = discoveredPeers.asStateFlow()

    override suspend fun sendFrame(neighborDeviceId: String, frame: ByteArray): MeshResult<Unit> =
        MeshResult.Failure(MeshError.TransportUnavailable("BLUETOOTH_LE"))

    override fun observeIncomingFrames(): Flow<IncomingFrame> = incomingFrames.asSharedFlow()
}
