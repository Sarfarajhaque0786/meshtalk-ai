package com.meshtalk.core.database.entity

import androidx.room.Entity

/**
 * Cached routing table, persisted so the RoutingEngine doesn't start from zero on every
 * app restart. Treated as a hint only - liveness must always be reconfirmed by the
 * RoutingEngine before use, since neighbors churn constantly in a mobile mesh.
 */
@Entity(tableName = "routing_table", primaryKeys = ["destinationDeviceId", "nextHopDeviceId"])
data class RoutingTableEntity(
    val destinationDeviceId: String,
    val nextHopDeviceId: String,
    val hopCount: Int,
    val protocol: String, // FLOOD | AODV | BATMAN | OLSR | HYBRID
    val linkQuality: Float,
    val lastUpdatedEpochMillis: Long
)
