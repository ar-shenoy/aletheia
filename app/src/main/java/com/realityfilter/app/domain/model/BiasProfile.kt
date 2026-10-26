package com.realityfilter.app.domain.model

import java.time.Instant

/**
 * Personal Bias Mirror — local-only model of the user's recurring cognitive patterns.
 *
 * Built purely from the content the user chooses to analyze.
 * Never leaves the device. Never sent anywhere.
 */
data class BiasProfile(
    val id: String = "local_profile",
    val patternFrequencies: Map<String, Int> = emptyMap(),   // patternId → count
    val topPatterns: List<PatternInsight> = emptyList(),
    val lastUpdated: Instant = Instant.now(),
    val totalAnalyses: Int = 0
)

data class PatternInsight(
    val patternId: String,
    val patternName: String,
    val category: PatternCategory,
    val occurrenceCount: Int,
    val averageConfidence: Float,
    val lastSeen: Instant,
    val sampleExplanations: List<String> = emptyList()
)

/**
 * A single recorded analysis session used to build the bias mirror.
 */
data class AnalysisSession(
    val id: String,
    val timestamp: Instant = Instant.now(),
    val sourceType: SourceType,
    val observationIds: List<String>,
    val textLength: Int = 0
)
