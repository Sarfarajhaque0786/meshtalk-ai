package com.meshtalk.domain.repository

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.MeshDevice
import kotlinx.coroutines.flow.Flow

/** Implemented in :data, delegating to :mesh's MeshManager/DiscoveryService/RoutingEngine. */
interface MeshRepository {
    fun observeNeighbors(): Flow<List<MeshDevice>>
    fun observeAllKnownDevices(): Flow<List<MeshDevice>>

    suspend fun startMesh(): MeshResult<Unit>
    suspend fun stopMesh(): MeshResult<Unit>

    /** Forces an active route re-discovery to [destinationDeviceId], bypassing route cache. */
    suspend fun rediscoverRoute(destinationDeviceId: String): MeshResult<Unit>
}
