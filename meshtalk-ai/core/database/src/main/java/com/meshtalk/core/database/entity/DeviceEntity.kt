package com.meshtalk.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A physical mesh node observed on any transport. Separate from [UserEntity] because
 * one person's identity key can be associated with several devices, and because the
 * routing engine needs node-level (not identity-level) reachability/reputation data.
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val deviceId: String,
    val ownerUserId: String?,
    val lastKnownTransport: String, // WIFI_DIRECT | WIFI_AWARE | BLE | UWB
    val lastRssi: Int?,
    val reputationScore: Float,
    val firstSeenEpochMillis: Long,
    val lastSeenEpochMillis: Long,
    val isDirectNeighbor: Boolean
)
