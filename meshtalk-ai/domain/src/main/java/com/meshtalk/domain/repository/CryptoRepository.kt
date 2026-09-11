package com.meshtalk.domain.repository

import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.domain.model.VerificationState

/** Implemented in :data, delegating to :crypto's IdentityManager/EncryptionEngine. */
interface CryptoRepository {
    /** Generates (or returns existing) Ed25519 identity + X25519 prekey bundle for this device. */
    suspend fun ensureLocalIdentity(): MeshResult<ByteArray> // returns public identity key

    /** Establishes or resumes a Double Ratchet session with [peerId] using their prekey bundle. */
    suspend fun ensureSession(peerId: String, peerPrekeyBundle: ByteArray): MeshResult<Unit>

    suspend fun encryptForPeer(peerId: String, plaintext: ByteArray): MeshResult<EncryptedEnvelope>

    suspend fun decryptFromPeer(peerId: String, envelope: EncryptedEnvelope): MeshResult<ByteArray>

    /** Out-of-band verification, e.g. after scanning the peer's QR identity code. */
    suspend fun markVerified(peerId: String, state: VerificationState): MeshResult<Unit>

    suspend fun rotateSignedPrekey(): MeshResult<Unit>
}

data class EncryptedEnvelope(
    val ciphertext: ByteArray,
    val nonce: ByteArray,
    val ratchetHeader: ByteArray
)
