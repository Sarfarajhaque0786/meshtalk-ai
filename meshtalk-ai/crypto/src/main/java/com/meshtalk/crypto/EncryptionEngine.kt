package com.meshtalk.crypto

import com.meshtalk.core.common.util.MeshResult

/**
 * Facade the rest of the app actually calls (via :data's CryptoRepositoryImpl) - it
 * composes [com.meshtalk.crypto.identity.IdentityManager] (who am I / who is this
 * peer) with [com.meshtalk.crypto.ratchet.RatchetSessionManager] (per-message key
 * derivation) and a symmetric cipher (AES-256-GCM by default; ChaCha20-Poly1305 as
 * the fallback for devices without AES hardware acceleration, matching the product
 * spec's requirement for both).
 *
 * Also owns replay protection: every decrypted message's ratchet header includes a
 * monotonic counter, and this engine rejects any counter already seen for that
 * peer's chain rather than leaving that check to callers.
 */
interface EncryptionEngine {
    suspend fun encrypt(peerId: String, plaintext: ByteArray): MeshResult<CipherEnvelope>
    suspend fun decrypt(peerId: String, envelope: CipherEnvelope): MeshResult<ByteArray>
}

data class CipherEnvelope(
    val ciphertext: ByteArray,
    val nonce: ByteArray,
    val ratchetHeader: ByteArray,
    val algorithm: SymmetricAlgorithm
)

enum class SymmetricAlgorithm { AES_256_GCM, CHACHA20_POLY1305 }
