package com.meshtalk.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.meshtalk.core.database.entity.RoutingTableEntity

@Dao
interface RoutingTableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(route: RoutingTableEntity)

    @Query("SELECT * FROM routing_table WHERE destinationDeviceId = :destinationId ORDER BY hopCount ASC, linkQuality DESC")
    suspend fun getRoutesTo(destinationId: String): List<RoutingTableEntity>

    @Query("DELETE FROM routing_table WHERE nextHopDeviceId = :neighborId")
    suspend fun invalidateRoutesThroughNeighbor(neighborId: String)
}
