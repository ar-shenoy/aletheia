package com.realityfilter.app.domain.model

import java.time.Instant

/**
 * A concrete instance of a pattern detected in some content.
 * This is what the user sees after analysis.
 */
data class Observation(
    val id: String,
    val patternId: String,
    val sourceType: SourceType,
    val sourceText: String? = null,
    val confidence: Float,          // 0.0 – 1.0
    val explanation: String,
    val matchedHints: List<String> = emptyList(),
    val timestamp: Instant = Instant.now(),
    val userNotes: String? = null,
    val isBookmarked: Boolean = false
)

enum class SourceType {
    MANUAL_TEXT,
    CAMERA_OCR,
    SHARE_SHEET,
    CONVERSATION,
    SCREEN_CAPTURE,
    OTHER
}
