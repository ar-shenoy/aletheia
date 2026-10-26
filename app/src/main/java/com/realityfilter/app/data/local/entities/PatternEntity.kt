package com.realityfilter.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.realityfilter.app.domain.model.PatternCategory
import com.realityfilter.app.domain.model.Severity

@Entity(tableName = "patterns")
@TypeConverters(Converters::class)
data class PatternEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: PatternCategory,
    val description: String,
    val detectionHints: List<String>,
    val examples: List<String>,
    val counterMeasures: List<String>,
    val historicalAnalogs: List<String>,
    val severity: Severity,
    val tags: List<String>,
    val version: Int
)

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(separator = "|||")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|||")

    @TypeConverter
    fun fromCategory(value: PatternCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): PatternCategory = PatternCategory.valueOf(value)

    @TypeConverter
    fun fromSeverity(value: Severity): String = value.name

    @TypeConverter
    fun toSeverity(value: String): Severity = Severity.valueOf(value)

    @TypeConverter
    fun fromSourceType(value: com.realityfilter.app.domain.model.SourceType): String = value.name

    @TypeConverter
    fun toSourceType(value: String): com.realityfilter.app.domain.model.SourceType =
        com.realityfilter.app.domain.model.SourceType.valueOf(value)
}
