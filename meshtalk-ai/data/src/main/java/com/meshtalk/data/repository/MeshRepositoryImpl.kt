package com.meshtalk.data.repository

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.MeshDevice
import com.meshtalk.domain.repository.MeshRepository
import com.meshtalk.mesh.MeshManager
import com.meshtalk.mesh.routing.RoutingEngine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeshRepositoryImpl @Inject constructor(
    private val meshManager: MeshManager,
    private val routingEngine: RoutingEngine
) : MeshRepository {

    override fun observeNeighbors(): Flow<List<MeshDevice>> = meshManager.observeNeighbors()

    override fun observeAllKnownDevices(): Flow<List<MeshDevice>> =
        // TODO: once DeviceDao is wired in here, union direct neighbors with previously
        // seen (currently out-of-range) devices for the developer dashboard's full
        // topology view. For now this mirrors observeNeighbors().
        meshManager.observeNeighbors()

    override suspend fun startMesh(): MeshResult<Unit> = meshManager.start()

    override suspend fun stopMesh(): MeshResult<Unit> = meshManager.stop()

    override suspend fun rediscoverRoute(destinationDeviceId: String): MeshResult<Unit> {
        routingEngine.invalidateRoutesThrough(destinationDeviceId)
        return when (routingEngine.findRoute(destinationDeviceId)) {
            is MeshResult.Success -> MeshResult.Success(Unit)
            is MeshResult.Failure -> MeshResult.Failure(
                com.meshtalk.core.common.util.MeshError.NoRouteToPeer(destinationDeviceId)
            )
        }
    }
}
