package com.realityfilter.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.realityfilter.app.data.local.entities.AnalysisSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisSessionDao {

    @Query("SELECT * FROM analysis_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<AnalysisSessionEntity>>

    @Query("SELECT * FROM analysis_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 50): Flow<List<AnalysisSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AnalysisSessionEntity)

    @Query("SELECT COUNT(*) FROM analysis_sessions")
    suspend fun getSessionCount(): Int

    @Query("DELETE FROM analysis_sessions")
    suspend fun clearAll()
}
