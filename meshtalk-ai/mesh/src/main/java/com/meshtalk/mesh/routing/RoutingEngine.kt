package com.meshtalk.mesh.routing

import com.meshtalk.core.common.util.MeshError
import com.meshtalk.core.common.util.MeshResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One route computed for a destination: which neighbor to hand the frame to next,
 * and which algorithm produced it (useful for the developer dashboard's routing
 * table view and for debugging route flaps).
 */
data class Route(
    val destinationDeviceId: String,
    val nextHopDeviceId: String,
    val hopCount: Int,
    val protocol: RoutingProtocol,
    val linkQuality: Float
)

enum class RoutingProtocol { FLOOD, AODV, BATMAN, OLSR, HYBRID }

/**
 * Route lookup + maintenance. [HybridRoutingEngine] is the production choice per the
 * product spec: it defaults to a BATMAN-like proactive protocol for the (usually
 * small, slow-changing) set of nearby always-on nodes, falls back to on-demand
 * AODV-style route discovery for rarely-contacted destinations, and always has FLOOD
 * available as the guaranteed-delivery mode for emergency broadcasts where "does
 * everyone eventually get this" matters more than routing efficiency.
 *
 * This file defines the contract and the algorithm-selection policy; the actual
 * per-protocol packet formats (RREQ/RREP for AODV, OGM for BATMAN, HELLO/TC for OLSR)
 * are a substantial follow-up best done one protocol at a time with its own tests.
 */
interface RoutingEngine {
    suspend fun findRoute(destinationDeviceId: String): MeshResult<Route>

    /** Called by MeshManager whenever a neighbor's presence/link quality changes. */
    suspend fun onNeighborUpdated(neighborDeviceId: String, linkQuality: Float, isPresent: Boolean)

    /** Emergency/broadcast path: always available even with an empty/stale routing table. */
    suspend fun floodTo(allNeighbors: List<String>): List<Route>

    suspend fun invalidateRoutesThrough(neighborDeviceId: String)
}

@Singleton
class HybridRoutingEngine @Inject constructor() : RoutingEngine {

    // TODO: back this with the persisted RoutingTableDao (via a :data repository) plus
    // an in-memory link-quality table maintained from onNeighborUpdated callbacks.

    override suspend fun findRoute(destinationDeviceId: String): MeshResult<Route> {
        return MeshResult.Failure(MeshError.NoRouteToPeer(destinationDeviceId))
    }

    override suspend fun onNeighborUpdated(neighborDeviceId: String, linkQuality: Float, isPresent: Boolean) {
        // TODO: update in-memory neighbor table; trigger BATMAN-style originator
        // message re-broadcast if this neighbor's link quality crossed a threshold.
    }

    override suspend fun floodTo(allNeighbors: List<String>): List<Route> =
        allNeighbors.map { neighborId ->
            Route(
                destinationDeviceId = neighborId,
                nextHopDeviceId = neighborId,
                hopCount = 1,
                protocol = RoutingProtocol.FLOOD,
                linkQuality = 1.0f
            )
        }

    override suspend fun invalidateRoutesThrough(neighborDeviceId: String) {
        // TODO: delete cached routes via RoutingTableDao.invalidateRoutesThroughNeighbor
    }
}
