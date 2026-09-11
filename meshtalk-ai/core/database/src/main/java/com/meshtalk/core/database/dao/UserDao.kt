package com.meshtalk.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.meshtalk.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun get(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE isBlocked = 0 ORDER BY displayName ASC")
    fun observeContacts(): Flow<List<UserEntity>>

    @Query("UPDATE users SET verificationState = :state WHERE id = :id")
    suspend fun setVerificationState(id: String, state: String)
}
