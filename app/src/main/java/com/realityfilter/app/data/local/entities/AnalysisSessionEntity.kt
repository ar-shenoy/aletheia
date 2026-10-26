package com.realityfilter.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.realityfilter.app.domain.model.SourceType

@Entity(tableName = "analysis_sessions")
@TypeConverters(Converters::class)
data class AnalysisSessionEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val sourceType: SourceType,
    val observationIds: List<String>,
    val textLength: Int,
    val sourcePreview: String? = null
)
