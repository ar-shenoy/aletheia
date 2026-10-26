package com.realityfilter.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realityfilter.app.domain.model.ConversationSession
import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern

@Composable
fun ConversationScreen(
    session: ConversationSession?,
    isListening: Boolean,
    continuousMode: Boolean,
    onContinuousChange: (Boolean) -> Unit,
    transcript: String,
    observations: List<Observation>,
    patterns: List<Pattern>,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onAnalyzeTranscript: () -> Unit,
    onEndSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Conversation mode", style = MaterialTheme.typography.displayLarge)
        Text(
            text = "Multi-turn sessions. Continuous mode keeps listening across pauses. Nothing leaves the device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Continuous multi-turn", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(checked = continuousMode, onCheckedChange = onContinuousChange)
        }
        if (session != null) {
            Text(
                text = "Session · ${session.segments.size} segments · ${session.fullTranscript.length} chars",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!isListening) {
                Button(onClick = onStartListening, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Mic, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Start")
                }
            } else {
                OutlinedButton(onClick = onStopListening, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Stop")
                }
            }
            Button(onClick = onAnalyzeTranscript, enabled = transcript.isNotBlank(), modifier = Modifier.weight(1f)) {
                Text("Analyze")
            }
        }
        if (session != null && session.segments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onEndSession, modifier = Modifier.fillMaxWidth()) {
                Text("End session")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isListening) "Listening…" else "Transcript",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = transcript.ifBlank { "Spoken words accumulate here across turns." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (transcript.isBlank()) 0.5f else 0.9f)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (observations.isEmpty()) "No signals yet" else "${observations.size} signals",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(observations, key = { it.id }) { obs ->
                val pattern = patterns.find { it.id == obs.patternId }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = pattern?.name ?: obs.patternId, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text(text = "Confidence ${(obs.confidence * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = obs.explanation.take(200), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
