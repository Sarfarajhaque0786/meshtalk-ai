package com.meshtalk.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.meshtalk.core.database.dao.DeviceDao
import com.meshtalk.core.database.dao.KeyDao
import com.meshtalk.core.database.dao.MessageDao
import com.meshtalk.core.database.dao.RoutingTableDao
import com.meshtalk.core.database.dao.UserDao
import com.meshtalk.core.database.entity.DeviceEntity
import com.meshtalk.core.database.entity.KeyEntity
import com.meshtalk.core.database.entity.MessageEntity
import com.meshtalk.core.database.entity.RoutingTableEntity
import com.meshtalk.core.database.entity.UserEntity

/**
 * The underlying SQLiteOpenHelper is created via SQLCipher's SupportFactory in
 * [com.meshtalk.core.database.di.DatabaseModule], so every table below is encrypted
 * at rest with a key held in the Android Keystore. Room itself is transport-agnostic
 * about that - it just sees a SupportSQLiteOpenHelper.Factory.
 *
 * Media, Network Cache, Settings, AI Cache and AI Models tables from the product spec
 * are intentionally not modelled yet in this skeleton: Media will reference encrypted
 * blobs on disk rather than storing bytes in SQLite, and the AI/cache tables depend on
 * decisions made in the :ai module that haven't been built out yet. Adding them is a
 * additive schema change (new @Entity + migration), not a redesign.
 */
@Database(
    entities = [
        MessageEntity::class,
        UserEntity::class,
        DeviceEntity::class,
        KeyEntity::class,
        RoutingTableEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MeshTalkDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
    abstract fun deviceDao(): DeviceDao
    abstract fun keyDao(): KeyDao
    abstract fun routingTableDao(): RoutingTableDao

    companion object {
        const val DATABASE_NAME = "meshtalk_encrypted.db"
    }
}
