package com.realityfilter.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realityfilter.app.domain.model.Pattern

@Composable
fun PatternDetailScreen(
    pattern: Pattern,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = pattern.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            SeverityBadge(severity = pattern.severity)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pattern.category.displayName(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionCard(title = "What it is") {
            Text(text = pattern.description, style = MaterialTheme.typography.bodyLarge)
        }

        if (pattern.detectionHints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SectionCard(title = "Detection signals") {
                pattern.detectionHints.forEach { hint ->
                    Text(
                        text = "• $hint",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        if (pattern.examples.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SectionCard(title = "Examples") {
                pattern.examples.forEach { ex ->
                    Text(
                        text = "• $ex",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        if (pattern.counterMeasures.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SectionCard(title = "Counter-moves") {
                pattern.counterMeasures.forEach { cm ->
                    Text(
                        text = "• $cm",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        if (pattern.historicalAnalogs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SectionCard(title = "Historical echoes") {
                pattern.historicalAnalogs.forEach { ha ->
                    Text(
                        text = "• $ha",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        if (pattern.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pattern.tags.joinToString(" · "),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
