package com.meshtalk.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.meshtalk.core.database.entity.KeyEntity

@Dao
interface KeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(key: KeyEntity)

    @Query("SELECT * FROM keys WHERE peerId = :peerId AND keyType = :keyType LIMIT 1")
    suspend fun get(peerId: String, keyType: String): KeyEntity?

    @Query("DELETE FROM keys WHERE peerId = :peerId AND keyType = :keyType")
    suspend fun delete(peerId: String, keyType: String)
}
