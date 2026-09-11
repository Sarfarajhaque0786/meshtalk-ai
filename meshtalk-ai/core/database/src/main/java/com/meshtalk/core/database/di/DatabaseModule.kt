package com.meshtalk.core.database.di

import android.content.Context
import androidx.room.Room
import com.meshtalk.core.database.MeshTalkDatabase
import com.meshtalk.core.database.dao.DeviceDao
import com.meshtalk.core.database.dao.KeyDao
import com.meshtalk.core.database.dao.MessageDao
import com.meshtalk.core.database.dao.RoutingTableDao
import com.meshtalk.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

/**
 * Provides the single [MeshTalkDatabase] instance, opened via SQLCipher's
 * [SupportFactory] so the on-disk file is AES-256 encrypted at the SQLite layer.
 *
 * The passphrase itself is never hardcoded: [DatabasePassphraseProvider] pulls (or
 * generates, on first run) a random 256-bit key sealed in the Android Keystore. If the
 * Keystore entry is ever lost (e.g. factory reset, different device), the database is
 * unrecoverable by design - that's the correct failure mode for a key held only in
 * hardware-backed storage, not a bug.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        passphraseProvider: DatabasePassphraseProvider
    ): MeshTalkDatabase {
        val factory = SupportFactory(passphraseProvider.getOrCreatePassphrase())
        return Room.databaseBuilder(context, MeshTalkDatabase::class.java, MeshTalkDatabase.DATABASE_NAME)
            .openHelperFactory(factory)
            // Real migrations are added per schema bump; skip-on-destructive is a
            // debug-only convenience gated separately in the app module's debug build type.
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideMessageDao(db: MeshTalkDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideUserDao(db: MeshTalkDatabase): UserDao = db.userDao()

    @Provides
    fun provideDeviceDao(db: MeshTalkDatabase): DeviceDao = db.deviceDao()

    @Provides
    fun provideKeyDao(db: MeshTalkDatabase): KeyDao = db.keyDao()

    @Provides
    fun provideRoutingTableDao(db: MeshTalkDatabase): RoutingTableDao = db.routingTableDao()
}
