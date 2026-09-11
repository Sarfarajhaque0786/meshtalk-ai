package com.meshtalk.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A contact known to this device. [identityPublicKey] is the Ed25519 verifying key;
 * [verificationState] tracks whether the user has confirmed it out-of-band (QR scan)
 * or is trusting Trust-On-First-Use.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val identityPublicKey: ByteArray,
    val verificationState: String, // UNVERIFIED | TOFU | QR_VERIFIED
    val avatarUri: String?,
    val lastSeenEpochMillis: Long?,
    val isBlocked: Boolean = false
)
