package com.realityfilter.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.realityfilter.app.data.local.entities.ObservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {

    @Query("SELECT * FROM observations ORDER BY timestamp DESC")
    fun getAllObservations(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedObservations(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations WHERE id = :id")
    suspend fun getObservationById(id: String): ObservationEntity?

    @Query("SELECT * FROM observations WHERE patternId = :patternId ORDER BY timestamp DESC")
    fun getObservationsForPattern(patternId: String): Flow<List<ObservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservation(observation: ObservationEntity)

    @Update
    suspend fun updateObservation(observation: ObservationEntity)

    @Query("DELETE FROM observations WHERE id = :id")
    suspend fun deleteObservation(id: String)

    @Query("DELETE FROM observations")
    suspend fun clearAll()
}
