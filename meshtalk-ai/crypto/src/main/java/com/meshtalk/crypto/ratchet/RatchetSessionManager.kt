package com.meshtalk.crypto.ratchet

import com.meshtalk.core.common.util.MeshResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One Double Ratchet session per peer conversation, giving each message its own
 * derived key (Perfect Forward Secrecy: compromising one message key doesn't expose
 * past or future ones) and rotating the root key on every DH ratchet step (Future
 * Secrecy: compromising current state doesn't expose messages once a fresh DH
 * exchange happens). Session state (chain keys, message counters, skipped-key cache
 * for out-of-order delivery - expected in a mesh where multi-hop delivery order
 * isn't guaranteed) persists via KeyEntity.encryptedRatchetState in :core:database.
 *
 * Left as a well-specified interface rather than a full implementation here: a
 * correct Double Ratchet needs careful handling of skipped message keys, header
 * encryption, and out-of-order/duplicate delivery - exactly the kind of protocol
 * code that should be built against a test vector suite (e.g. Signal's published
 * ones) rather than scaffolded alongside the rest of the module tree.
 */
interface RatchetSessionManager {
    suspend fun initSession(peerId: String, sharedSecret: ByteArray, isInitiator: Boolean): MeshResult<Unit>
    suspend fun encrypt(peerId: String, plaintext: ByteArray): MeshResult<RatchetMessage>
    suspend fun decrypt(peerId: String, message: RatchetMessage): MeshResult<ByteArray>
    suspend fun hasSession(peerId: String): Boolean
}

data class RatchetMessage(
    val header: ByteArray,
    val ciphertext: ByteArray,
    val nonce: ByteArray
)

@Singleton
class DoubleRatchetSessionManager @Inject constructor() : RatchetSessionManager {

    override suspend fun initSession(peerId: String, sharedSecret: ByteArray, isInitiator: Boolean): MeshResult<Unit> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(
            NotImplementedError("Double Ratchet session init not yet implemented")
        ))

    override suspend fun encrypt(peerId: String, plaintext: ByteArray): MeshResult<RatchetMessage> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))

    override suspend fun decrypt(peerId: String, message: RatchetMessage): MeshResult<ByteArray> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))

    override suspend fun hasSession(peerId: String): Boolean = false
}
