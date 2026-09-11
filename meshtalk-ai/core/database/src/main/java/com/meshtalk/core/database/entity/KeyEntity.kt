package com.meshtalk.core.database.entity

import androidx.room.Entity

/**
 * Metadata about key material, e.g. per-peer Double Ratchet session state (chain keys,
 * message counters) and one-time prekey bookkeeping. Long-term private identity/signing
 * keys are NOT stored here - they live in the Android Keystore (hardware-backed where
 * available) and are referenced only by [keystoreAlias]. This table only ever holds
 * material that is itself encrypted at the SQLCipher layer, and ratchet state that is
 * useless without the corresponding Keystore-protected root key.
 */
@Entity(tableName = "keys", primaryKeys = ["peerId", "keyType"])
data class KeyEntity(
    val peerId: String,
    val keyType: String, // IDENTITY | SIGNED_PREKEY | ONE_TIME_PREKEY | RATCHET_STATE
    val keystoreAlias: String?,
    val publicKeyBytes: ByteArray?,
    val encryptedRatchetState: ByteArray?,
    val createdAtEpochMillis: Long,
    val rotatedAtEpochMillis: Long?
)
