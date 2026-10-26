package com.realityfilter.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.realityfilter.app.data.local.entities.ConversationSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: ConversationSessionEntity)

    @Update
    suspend fun update(session: ConversationSessionEntity)

    @Query("SELECT * FROM conversation_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ConversationSessionEntity?

    @Query("SELECT * FROM conversation_sessions WHERE isActive = 1 ORDER BY startedAtEpochMs DESC LIMIT 1")
    suspend fun getActive(): ConversationSessionEntity?

    @Query("SELECT * FROM conversation_sessions ORDER BY startedAtEpochMs DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<ConversationSessionEntity>>

    @Query("SELECT * FROM conversation_sessions ORDER BY startedAtEpochMs DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<ConversationSessionEntity>

    @Query("DELETE FROM conversation_sessions WHERE id = :id")
    suspend fun delete(id: String)
}
