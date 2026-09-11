package com.meshtalk.crypto.identity

import com.meshtalk.core.common.util.MeshResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns this device's long-term Ed25519 identity keypair and the rotating X25519
 * prekey bundle (signed prekey + a batch of one-time prekeys) used to bootstrap
 * Double Ratchet sessions with peers, following the same shape as the Signal/X3DH
 * prekey bundle model.
 *
 * Private key material never leaves this class as raw bytes where avoidable - keys
 * are generated inside and referenced by Android Keystore alias, with Tink's
 * AndroidKeystoreKmsClient wrapping keys that must be exportable (e.g. for backup)
 * so they're still encrypted-at-rest even outside the Keystore boundary.
 *
 * This is a real security-critical component intentionally left as a well-specified
 * stub: hooking up Tink's Ed25519PrivateKey / X25519 HPKE primitives correctly
 * (nonce handling, prekey exhaustion/replenishment, secure deletion of used one-time
 * prekeys) deserves its own focused pass with tests, not to be rushed alongside
 * scaffolding fifteen other modules.
 */
interface IdentityManager {
    suspend fun getOrCreateIdentityKeyPair(): MeshResult<PublicIdentity>
    suspend fun getSignedPrekeyBundle(): MeshResult<PrekeyBundle>
    suspend fun consumeOneTimePrekey(): MeshResult<ByteArray?>
    suspend fun rotateSignedPrekey(): MeshResult<Unit>
    suspend fun sign(data: ByteArray): MeshResult<ByteArray>
    suspend fun verify(data: ByteArray, signature: ByteArray, signerPublicKey: ByteArray): MeshResult<Boolean>
}

data class PublicIdentity(val ed25519PublicKey: ByteArray)

data class PrekeyBundle(
    val identityKey: ByteArray,
    val signedPrekey: ByteArray,
    val signedPrekeySignature: ByteArray,
    val oneTimePrekey: ByteArray?
)

@Singleton
class TinkIdentityManager @Inject constructor() : IdentityManager {

    override suspend fun getOrCreateIdentityKeyPair(): MeshResult<PublicIdentity> {
        // TODO: com.google.crypto.tink.signature.SignatureKeyTemplates.ED25519,
        // persisted via AndroidKeysetManager with a Keystore master key alias.
        return MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(
            NotImplementedError("IdentityManager key generation not yet wired to Tink/Keystore")
        ))
    }

    override suspend fun getSignedPrekeyBundle(): MeshResult<PrekeyBundle> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))

    override suspend fun consumeOneTimePrekey(): MeshResult<ByteArray?> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))

    override suspend fun rotateSignedPrekey(): MeshResult<Unit> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))

    override suspend fun sign(data: ByteArray): MeshResult<ByteArray> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))

    override suspend fun verify(data: ByteArray, signature: ByteArray, signerPublicKey: ByteArray): MeshResult<Boolean> =
        MeshResult.Failure(com.meshtalk.core.common.util.MeshError.Unknown(NotImplementedError()))
}
