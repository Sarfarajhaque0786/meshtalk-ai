package com.meshtalk.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.meshtalk.core.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(device: DeviceEntity)

    @Query("SELECT * FROM devices WHERE isDirectNeighbor = 1")
    fun observeDirectNeighbors(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices ORDER BY reputationScore DESC")
    fun observeAllKnownDevices(): Flow<List<DeviceEntity>>

    @Query("UPDATE devices SET reputationScore = :score WHERE deviceId = :deviceId")
    suspend fun updateReputation(deviceId: String, score: Float)

    @Query("DELETE FROM devices WHERE lastSeenEpochMillis < :staleBeforeEpochMillis")
    suspend fun pruneStale(staleBeforeEpochMillis: Long): Int
}
