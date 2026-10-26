package com.realityfilter.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.Severity

@Composable
fun HomeScreen(
    patterns: List<Pattern>,
    onAnalyze: (String) -> List<Observation>,
    onToggleBookmark: (Observation) -> Unit = {},
    initialText: String? = null,
    isOcrRunning: Boolean = false,
    onPickImage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf(initialText ?: "") }
    var results by remember { mutableStateOf<List<Observation>>(emptyList()) }
    var hasAnalyzed by remember { mutableStateOf(false) }
    var bookmarkedIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(initialText) {
        if (!initialText.isNullOrBlank()) {
            inputText = initialText
            results = onAnalyze(initialText)
            hasAnalyzed = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Aletheia",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Paste any claim, article excerpt, speech, or message. Analysis runs fully on-device.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Paste text here…") },
            maxLines = 8,
            trailingIcon = {
                if (inputText.isNotEmpty()) {
                    IconButton(onClick = {
                        inputText = ""
                        results = emptyList()
                        hasAnalyzed = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    results = onAnalyze(inputText)
                    hasAnalyzed = true
                },
                modifier = Modifier.weight(1f),
                enabled = inputText.isNotBlank() && !isOcrRunning
            ) {
                Text("Analyze locally")
            }
            OutlinedButton(
                onClick = onPickImage,
                enabled = !isOcrRunning
            ) {
                Icon(Icons.Default.Image, contentDescription = "OCR from image")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isOcrRunning) "Reading…" else "Image")
            }
        }

        if (hasAnalyzed) {
            if (results.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = "No strong pattern matches found with the current library. This is normal for short or neutral text. Try a longer claim or a piece of persuasive writing.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Text(
                    text = "${results.size} signal${if (results.size == 1) "" else "s"} detected",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                results.forEach { observation ->
                    val pattern = patterns.find { it.id == observation.patternId }
                    ObservationCard(
                        observation = observation,
                        pattern = pattern,
                        isBookmarked = observation.id in bookmarkedIds,
                        onToggleBookmark = {
                            bookmarkedIds = if (observation.id in bookmarkedIds) {
                                bookmarkedIds - observation.id
                            } else {
                                bookmarkedIds + observation.id
                            }
                            onToggleBookmark(observation)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${patterns.size} patterns loaded · fully offline",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
        )
    }
}

@Composable
private fun ObservationCard(
    observation: Observation,
    pattern: Pattern?,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
    val severity = pattern?.severity ?: Severity.MEDIUM
    val accent = severityColor(severity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = pattern?.name ?: observation.patternId,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Confidence ${(observation.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = severity.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = observation.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun severityColor(severity: Severity): Color {
    return when (severity) {
        Severity.LOW -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
        Severity.MEDIUM -> MaterialTheme.colorScheme.primary
        Severity.HIGH -> Color(0xFFE07A5F)
        Severity.CRITICAL -> MaterialTheme.colorScheme.error
    }
}
