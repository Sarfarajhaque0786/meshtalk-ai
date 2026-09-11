package com.meshtalk.data.repository

import com.meshtalk.core.common.util.MeshError
import com.meshtalk.core.common.util.MeshResult
import com.meshtalk.core.database.dao.UserDao
import com.meshtalk.crypto.identity.IdentityManager
import com.meshtalk.crypto.ratchet.RatchetSessionManager
import com.meshtalk.crypto.ratchet.RatchetMessage
import com.meshtalk.domain.model.VerificationState
import com.meshtalk.domain.repository.CryptoRepository
import com.meshtalk.domain.repository.EncryptedEnvelope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoRepositoryImpl @Inject constructor(
    private val identityManager: IdentityManager,
    private val ratchetSessionManager: RatchetSessionManager,
    private val userDao: UserDao
) : CryptoRepository {

    override suspend fun ensureLocalIdentity(): MeshResult<ByteArray> =
        when (val result = identityManager.getOrCreateIdentityKeyPair()) {
            is MeshResult.Success -> MeshResult.Success(result.data.ed25519PublicKey)
            is MeshResult.Failure -> result
        }

    override suspend fun ensureSession(peerId: String, peerPrekeyBundle: ByteArray): MeshResult<Unit> {
        if (ratchetSessionManager.hasSession(peerId)) return MeshResult.Success(Unit)
        // TODO: run X3DH against peerPrekeyBundle to derive the initial shared secret,
        // then hand it to ratchetSessionManager.initSession. Depends on IdentityManager's
        // Tink wiring landing first (see :crypto TODOs).
        return MeshResult.Failure(MeshError.Unknown(NotImplementedError("X3DH handshake not yet implemented")))
    }

    override suspend fun encryptForPeer(peerId: String, plaintext: ByteArray): MeshResult<EncryptedEnvelope> =
        when (val result = ratchetSessionManager.encrypt(peerId, plaintext)) {
            is MeshResult.Success -> MeshResult.Success(
                EncryptedEnvelope(
                    ciphertext = result.data.ciphertext,
                    nonce = result.data.nonce,
                    ratchetHeader = result.data.header
                )
            )
            is MeshResult.Failure -> result
        }

    override suspend fun decryptFromPeer(peerId: String, envelope: EncryptedEnvelope): MeshResult<ByteArray> =
        ratchetSessionManager.decrypt(
            peerId,
            RatchetMessage(header = envelope.ratchetHeader, ciphertext = envelope.ciphertext, nonce = envelope.nonce)
        )

    override suspend fun markVerified(peerId: String, state: VerificationState): MeshResult<Unit> {
        userDao.setVerificationState(peerId, state.name)
        return MeshResult.Success(Unit)
    }

    override suspend fun rotateSignedPrekey(): MeshResult<Unit> = identityManager.rotateSignedPrekey()
}
