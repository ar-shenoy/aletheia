package com.realityfilter.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_sessions")
data class ConversationSessionEntity(
    @PrimaryKey val id: String,
    val startedAtEpochMs: Long,
    val endedAtEpochMs: Long? = null,
    val fullTranscript: String = "",
    val segmentJson: String = "[]",
    val observationIdsJson: String = "[]",
    val isActive: Boolean = true
)
