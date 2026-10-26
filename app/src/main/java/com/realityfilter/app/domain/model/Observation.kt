package com.realityfilter.app.domain.model

import java.time.Instant

data class Observation(
    val id: String,
    val patternId: String,
    val sourceType: SourceType,
    val sourceText: String? = null,
    val confidence: Float,
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
    LIVE_CAMERA,
    SCREEN_CAPTURE,
    OTHER
}
