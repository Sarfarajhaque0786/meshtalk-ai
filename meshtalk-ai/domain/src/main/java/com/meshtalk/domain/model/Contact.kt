package com.meshtalk.domain.model

data class Contact(
    val id: String,
    val displayName: String,
    val avatarUri: String?,
    val verificationState: VerificationState,
    val lastSeen: Long?,
    val isBlocked: Boolean
)

enum class VerificationState { UNVERIFIED, TRUST_ON_FIRST_USE, QR_VERIFIED }

/**
 * A live mesh node, as seen by the RoutingEngine/DiscoveryService - distinct from
 * [Contact] because a device may be a not-yet-identified neighbor relaying traffic
 * for someone else, with no associated Contact at all.
 */
data class MeshDevice(
    val deviceId: String,
    val ownerContactId: String?,
    val transport: Transport,
    val rssi: Int?,
    val reputationScore: Float,
    val isDirectNeighbor: Boolean,
    val lastSeen: Long
)

enum class Transport { WIFI_DIRECT, WIFI_AWARE, BLUETOOTH_LE, UWB }
