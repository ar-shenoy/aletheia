package com.realityfilter.app.data.repository

import com.realityfilter.app.data.local.AnalysisSessionDao
import com.realityfilter.app.data.local.ObservationDao
import com.realityfilter.app.data.local.entities.AnalysisSessionEntity
import com.realityfilter.app.data.local.entities.ObservationEntity
import com.realityfilter.app.domain.model.BiasProfile
import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.PatternInsight
import com.realityfilter.app.domain.model.SourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID

class ObservationRepository(
    private val observationDao: ObservationDao,
    private val sessionDao: AnalysisSessionDao
) {

    fun getBookmarkedObservations(): Flow<List<Observation>> =
        observationDao.getBookmarkedObservations().map { list ->
            list.map { it.toDomain() }
        }

    fun getAllObservations(): Flow<List<Observation>> =
        observationDao.getAllObservations().map { list ->
            list.map { it.toDomain() }
        }

    suspend fun saveAnalysis(
        observations: List<Observation>,
        sourceType: SourceType,
        sourceText: String?
    ) {
        if (observations.isEmpty()) return

        observations.forEach { obs ->
            observationDao.insertObservation(obs.toEntity())
        }

        val session = AnalysisSessionEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            sourceType = sourceType,
            observationIds = observations.map { it.id },
            textLength = sourceText?.length ?: 0,
            sourcePreview = sourceText?.take(120)
        )
        sessionDao.insertSession(session)
    }

    suspend fun toggleBookmark(observation: Observation) {
        val entity = observation.toEntity().copy(isBookmarked = !observation.isBookmarked)
        observationDao.updateObservation(entity)
    }

    suspend fun buildBiasProfile(patterns: List<Pattern>): BiasProfile {
        val allObs = observationDao.getAllObservations().first()
        if (allObs.isEmpty()) {
            return BiasProfile(totalAnalyses = sessionDao.getSessionCount())
        }

        val byPattern = allObs.groupBy { it.patternId }
        val insights = byPattern.map { (patternId, obsList) ->
            val pattern = patterns.find { it.id == patternId }
            PatternInsight(
                patternId = patternId,
                patternName = pattern?.name ?: patternId,
                category = pattern?.category ?: com.realityfilter.app.domain.model.PatternCategory.OTHER,
                occurrenceCount = obsList.size,
                averageConfidence = obsList.map { it.confidence }.average().toFloat(),
                lastSeen = Instant.ofEpochMilli(obsList.maxOf { it.timestamp }),
                sampleExplanations = obsList.take(2).map { it.explanation.take(160) }
            )
        }.sortedByDescending { it.occurrenceCount }

        val frequencies = byPattern.mapValues { it.value.size }

        return BiasProfile(
            patternFrequencies = frequencies,
            topPatterns = insights.take(12),
            lastUpdated = Instant.now(),
            totalAnalyses = sessionDao.getSessionCount()
        )
    }

    private fun ObservationEntity.toDomain() = Observation(
        id = id,
        patternId = patternId,
        sourceType = sourceType,
        sourceText = sourceText,
        confidence = confidence,
        explanation = explanation,
        matchedHints = matchedHints,
        timestamp = Instant.ofEpochMilli(timestamp),
        userNotes = userNotes,
        isBookmarked = isBookmarked
    )

    private fun Observation.toEntity() = ObservationEntity(
        id = id,
        patternId = patternId,
        sourceType = sourceType,
        sourceText = sourceText,
        confidence = confidence,
        explanation = explanation,
        matchedHints = matchedHints,
        timestamp = timestamp.toEpochMilli(),
        userNotes = userNotes,
        isBookmarked = isBookmarked
    )
}
