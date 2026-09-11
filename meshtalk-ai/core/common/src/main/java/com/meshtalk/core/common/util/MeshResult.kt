package com.meshtalk.core.common.util

/**
 * Result wrapper used at every module boundary (domain use cases, repository calls, mesh/crypto
 * operations) so callers are forced to handle failure explicitly rather than relying on
 * exceptions crossing coroutine/module boundaries.
 */
sealed interface MeshResult<out T> {
    data class Success<T>(val data: T) : MeshResult<T>
    data class Failure(val error: MeshError) : MeshResult<Nothing>
}

/**
 * Deliberately not a 1:1 wrap of exceptions - each case maps to something the UI or routing
 * layer can act on directly (e.g. show "no route to peer" vs. a generic error toast).
 */
sealed class MeshError(val message: String, val cause: Throwable? = null) {
    data class NoRouteToPeer(val peerId: String) :
        MeshError("No route to peer $peerId")

    data class TransportUnavailable(val transport: String) :
        MeshError("Transport unavailable: $transport")

    data class DecryptionFailed(val reason: String) :
        MeshError("Decryption failed: $reason")

    data class IdentityVerificationFailed(val peerId: String) :
        MeshError("Could not verify identity of $peerId")

    data class StorageError(val detail: String, val throwable: Throwable? = null) :
        MeshError("Storage error: $detail", throwable)

    data class Unknown(val throwable: Throwable) :
        MeshError("Unexpected error: ${throwable.message}", throwable)
}

inline fun <T, R> MeshResult<T>.map(transform: (T) -> R): MeshResult<R> = when (this) {
    is MeshResult.Success -> MeshResult.Success(transform(data))
    is MeshResult.Failure -> this
}

inline fun <T> MeshResult<T>.onSuccess(action: (T) -> Unit): MeshResult<T> {
    if (this is MeshResult.Success) action(data)
    return this
}

inline fun <T> MeshResult<T>.onFailure(action: (MeshError) -> Unit): MeshResult<T> {
    if (this is MeshResult.Failure) action(error)
    return this
}
