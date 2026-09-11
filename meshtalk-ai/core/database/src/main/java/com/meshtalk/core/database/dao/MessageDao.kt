package com.meshtalk.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.meshtalk.core.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // replay protection: duplicate id = drop
    suspend fun insert(message: MessageEntity): Long

    @Update
    suspend fun update(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY sentAtEpochMillis ASC")
    fun observeConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE deliveryState = 'QUEUED' ORDER BY sentAtEpochMillis ASC")
    suspend fun getQueuedForRetry(): List<MessageEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM messages WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query("DELETE FROM messages WHERE expiresAtEpochMillis IS NOT NULL AND expiresAtEpochMillis <= :nowEpochMillis")
    suspend fun purgeExpired(nowEpochMillis: Long): Int
}
