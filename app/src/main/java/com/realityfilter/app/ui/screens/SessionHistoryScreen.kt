package com.realityfilter.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realityfilter.app.domain.model.ConversationSession
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SessionHistoryScreen(
    sessions: List<ConversationSession>,
    onBack: () -> Unit,
    onOpenSession: (ConversationSession) -> Unit,
    modifier: Modifier = Modifier
) {
    val fmt = DateTimeFormatter.ofPattern("MMM d, HH:mm").withZone(ZoneId.systemDefault())

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Past conversations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (sessions.isEmpty()) {
            Text(
                text = "No saved sessions yet.\n\nUse Talk \u2192 Start, speak, Analyze, then End session.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(sessions, key = { it.id }) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenSession(session) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = fmt.format(session.startedAt), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = session.fullTranscript.ifBlank { "(empty)" }.take(160), style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${session.segments.size} segments \u00b7 ${session.observationIds.size} signals",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                    }
                }
            }
        }
    }
}
