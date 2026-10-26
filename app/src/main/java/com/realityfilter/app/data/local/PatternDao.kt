package com.realityfilter.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.realityfilter.app.data.local.entities.PatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {

    @Query("SELECT * FROM patterns ORDER BY name ASC")
    fun getAllPatterns(): Flow<List<PatternEntity>>

    @Query("SELECT * FROM patterns WHERE id = :id")
    suspend fun getPatternById(id: String): PatternEntity?

    @Query("SELECT * FROM patterns WHERE category = :category")
    fun getPatternsByCategory(category: String): Flow<List<PatternEntity>>

    @Query("SELECT * FROM patterns WHERE tags LIKE '%' || :tag || '%'")
    fun getPatternsByTag(tag: String): Flow<List<PatternEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatterns(patterns: List<PatternEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: PatternEntity)

    @Query("DELETE FROM patterns")
    suspend fun clearAll()
}
