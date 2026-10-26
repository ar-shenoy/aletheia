package com.realityfilter.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.realityfilter.app.domain.model.SourceType

@Entity(tableName = "observations")
@TypeConverters(Converters::class)
data class ObservationEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val sourceType: SourceType,
    val sourceText: String?,
    val confidence: Float,
    val explanation: String,
    val matchedHints: List<String>,
    val timestamp: Long,
    val userNotes: String?,
    val isBookmarked: Boolean
)
