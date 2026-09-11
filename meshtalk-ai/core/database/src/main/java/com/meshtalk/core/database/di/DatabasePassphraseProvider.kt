package com.meshtalk.core.database.di

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates a random 256-bit SQLCipher passphrase on first run and seals it via
 * [EncryptedSharedPreferences], whose own key lives in the Android Keystore
 * (hardware-backed on devices that support StrongBox). Application code never
 * chooses or sees a human-memorable password for the database - this is deliberate:
 * database-at-rest encryption and user authentication (PIN/biometric app lock) are
 * separate concerns handled by different layers (see :crypto SecurityManager).
 */
@Singleton
class DatabasePassphraseProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "meshtalk_db_passphrase_store",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getOrCreatePassphrase(): ByteArray {
        val existing = prefs.getString(KEY_ALIAS, null)
        if (existing != null) {
            return existing.split(",").map { it.toByte() }.toByteArray()
        }
        val generated = ByteArray(32).also { SecureRandom().nextBytes(it) }
        prefs.edit().putString(KEY_ALIAS, generated.joinToString(",")).apply()
        return generated
    }

    private companion object {
        const val KEY_ALIAS = "sqlcipher_passphrase_v1"
    }
}
