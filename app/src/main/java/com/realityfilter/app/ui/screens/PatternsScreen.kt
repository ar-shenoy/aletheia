package com.realityfilter.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.PatternCategory
import com.realityfilter.app.domain.model.Severity

@Composable
fun PatternsScreen(
    patterns: List<Pattern>,
    onPatternClick: (Pattern) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<PatternCategory?>(null) }

    val filtered = if (selectedCategory == null) {
        patterns
    } else {
        patterns.filter { it.category == selectedCategory }
    }

    val categories = PatternCategory.entries.filter { cat ->
        patterns.any { it.category == cat }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Pattern Library",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        Text(
            text = "${patterns.size} detectors · fully local",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All") }
            )
            categories.take(4).forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = {
                        selectedCategory = if (selectedCategory == cat) null else cat
                    },
                    label = { Text(cat.displayName()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { pattern ->
                PatternCard(
                    pattern = pattern,
                    onClick = { onPatternClick(pattern) }
                )
            }
        }
    }
}

@Composable
private fun PatternCard(
    pattern: Pattern,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pattern.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                SeverityBadge(severity = pattern.severity)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pattern.category.displayName(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 3
            )

            if (pattern.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pattern.tags.take(4).joinToString(" · "),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SeverityBadge(severity: Severity) {
    val (label, color) = when (severity) {
        Severity.LOW -> "Low" to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        Severity.MEDIUM -> "Med" to MaterialTheme.colorScheme.primary
        Severity.HIGH -> "High" to MaterialTheme.colorScheme.error
        Severity.CRITICAL -> "Crit" to MaterialTheme.colorScheme.error
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

fun PatternCategory.displayName(): String = when (this) {
    PatternCategory.COGNITIVE_BIAS -> "Cognitive Bias"
    PatternCategory.RHETORICAL_DEVICE -> "Rhetoric"
    PatternCategory.PERSUASION_TECHNIQUE -> "Persuasion"
    PatternCategory.STATISTICAL_FALLACY -> "Statistics"
    PatternCategory.NARRATIVE_STRUCTURE -> "Narrative"
    PatternCategory.POWER_DYNAMIC -> "Power"
    PatternCategory.MEDIA_TECHNIQUE -> "Media"
    PatternCategory.OTHER -> "Other"
}
