package com.realityfilter.app.domain.model

import java.time.Instant
import java.util.UUID

data class ConversationSession(
    val id: String = UUID.randomUUID().toString(),
    val startedAt: Instant = Instant.now(),
    val endedAt: Instant? = null,
    val segments: List<TranscriptSegment> = emptyList(),
    val observationIds: List<String> = emptyList(),
    val isActive: Boolean = true
) {
    val fullTranscript: String
        get() = segments.joinToString(" ") { it.text }.trim()

    fun appendSegment(text: String, isFinal: Boolean): ConversationSession {
        if (text.isBlank()) return this
        val seg = TranscriptSegment(
            id = UUID.randomUUID().toString(),
            text = text.trim(),
            timestamp = Instant.now(),
            isFinal = isFinal
        )
        return copy(segments = segments + seg)
    }

    fun withObservations(ids: List<String>): ConversationSession =
        copy(observationIds = (observationIds + ids).distinct())

    fun end(): ConversationSession =
        copy(isActive = false, endedAt = Instant.now())
}

data class TranscriptSegment(
    val id: String,
    val text: String,
    val timestamp: Instant,
    val isFinal: Boolean
)
