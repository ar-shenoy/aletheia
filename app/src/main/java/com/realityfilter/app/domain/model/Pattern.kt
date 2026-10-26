package com.realityfilter.app.domain.model

/**
 * A detectable pattern in human communication or information.
 * Patterns are the core knowledge unit of Aletheia.
 */
data class Pattern(
    val id: String,
    val name: String,
    val category: PatternCategory,
    val description: String,
    val detectionHints: List<String>,
    val examples: List<String>,
    val counterMeasures: List<String> = emptyList(),
    val historicalAnalogs: List<String> = emptyList(),
    val severity: Severity = Severity.MEDIUM,
    val tags: List<String> = emptyList(),
    val version: Int = 1
)

enum class PatternCategory {
    COGNITIVE_BIAS,
    RHETORICAL_DEVICE,
    PERSUASION_TECHNIQUE,
    STATISTICAL_FALLACY,
    NARRATIVE_STRUCTURE,
    POWER_DYNAMIC,
    MEDIA_TECHNIQUE,
    OTHER
}

enum class Severity {
    LOW,       // Mild distortion, worth noticing
    MEDIUM,    // Common and impactful
    HIGH,      // Strong manipulative potential
    CRITICAL   // High risk of significant harm or deception
}
