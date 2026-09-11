package com.meshtalk.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "meshtalk_settings")

/**
 * Non-sensitive app preferences (theme, language, battery/low-bandwidth mode toggles).
 * Nothing security-relevant belongs here - key material and ratchet state live in
 * :core:database (SQLCipher) and the Android Keystore, not in DataStore.
 *
 * Implemented with Preferences DataStore for the skeleton rather than Proto DataStore
 * to avoid pulling in the protobuf codegen toolchain for scaffolding purposes; the spec
 * calls for Proto DataStore, and migrating this repository's interface to a generated
 * `Settings` proto message is a mechanical follow-up, not an architecture change - callers
 * only see [SettingsRepository]'s Flow-based API either way.
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
        val LOW_BANDWIDTH_MODE = booleanPreferencesKey("low_bandwidth_mode")
        val BATTERY_SAVER_MODE = booleanPreferencesKey("battery_saver_mode")
        val ROUTING_PROTOCOL = stringPreferencesKey("routing_protocol") // FLOOD|AODV|BATMAN|OLSR|HYBRID
        val MAX_TTL = intPreferencesKey("max_ttl")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }
    val languageTag: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE_TAG] ?: "en" }
    val isLowBandwidthMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.LOW_BANDWIDTH_MODE] ?: false }
    val isBatterySaverMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.BATTERY_SAVER_MODE] ?: false }
    val routingProtocol: Flow<String> = context.dataStore.data.map { it[Keys.ROUTING_PROTOCOL] ?: "HYBRID" }
    val maxTtl: Flow<Int> = context.dataStore.data.map { it[Keys.MAX_TTL] ?: DEFAULT_MAX_TTL }

    suspend fun setDarkMode(enabled: Boolean) = context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    suspend fun setLanguageTag(tag: String) = context.dataStore.edit { it[Keys.LANGUAGE_TAG] = tag }
    suspend fun setLowBandwidthMode(enabled: Boolean) = context.dataStore.edit { it[Keys.LOW_BANDWIDTH_MODE] = enabled }
    suspend fun setBatterySaverMode(enabled: Boolean) = context.dataStore.edit { it[Keys.BATTERY_SAVER_MODE] = enabled }
    suspend fun setRoutingProtocol(protocol: String) = context.dataStore.edit { it[Keys.ROUTING_PROTOCOL] = protocol }
    suspend fun setMaxTtl(ttl: Int) = context.dataStore.edit { it[Keys.MAX_TTL] = ttl }

    private companion object {
        const val DEFAULT_MAX_TTL = 8 // hop limit before a message is dropped, tuned in RoutingEngine
    }
}
