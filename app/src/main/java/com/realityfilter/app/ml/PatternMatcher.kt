package com.realityfilter.app.ml

import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.SourceType
import java.util.UUID

/**
 * Transparent heuristic pattern matcher for Aletheia v0.1.
 *
 * Design goals:
 * - Fully inspectable (no black-box)
 * - Fast on-device
 * - Prefer precision over recall for early versions
 * - Easy to replace later with embeddings + small local LLM
 *
 * Matching strategy:
 * 1. Exact phrase match against detectionHints (strong signal)
 * 2. Token overlap scoring for partial matches
 * 3. Confidence calibrated by how many independent hints fire
 */
class PatternMatcher {

    fun analyze(text: String, patterns: List<Pattern>): List<Observation> {
        if (text.isBlank()) return emptyList()

        val normalized = text.lowercase().trim()
        val tokens = tokenize(normalized)
        val observations = mutableListOf<Observation>()

        for (pattern in patterns) {
            val matchedHints = mutableListOf<String>()
            var score = 0f

            for (hint in pattern.detectionHints) {
                val hintLower = hint.lowercase()
                when {
                    // Strong: full phrase present
                    normalized.contains(hintLower) -> {
                        matchedHints.add(hint)
                        score += 1.0f
                    }
                    // Medium: significant token overlap
                    else -> {
                        val hintTokens = tokenize(hintLower)
                        if (hintTokens.isNotEmpty()) {
                            val overlap = hintTokens.count { it in tokens }.toFloat() / hintTokens.size
                            if (overlap >= 0.6f) {
                                matchedHints.add("~ $hint")
                                score += overlap * 0.7f
                            }
                        }
                    }
                }
            }

            if (matchedHints.isNotEmpty() && score >= 0.5f) {
                val confidence = (score / pattern.detectionHints.size.coerceAtLeast(1))
                    .coerceIn(0.25f, 0.92f)

                observations.add(
                    Observation(
                        id = UUID.randomUUID().toString(),
                        patternId = pattern.id,
                        sourceType = SourceType.MANUAL_TEXT,
                        sourceText = text.take(600),
                        confidence = confidence,
                        explanation = buildExplanation(pattern, matchedHints),
                        matchedHints = matchedHints
                    )
                )
            }
        }

        return observations.sortedByDescending { it.confidence }
    }

    private fun tokenize(text: String): Set<String> {
        return text
            .split(Regex("[\\s\\p{Punct}]+"))
            .map { it.trim() }
            .filter { it.length > 2 }
            .toSet()
    }

    private fun buildExplanation(pattern: Pattern, matchedHints: List<String>): String {
        return buildString {
            append("Signal: ${pattern.name}\n\n")
            append(pattern.description)
            append("\n\nMatched indicators:\n")
            matchedHints.take(5).forEach { append("• $it\n") }
            if (pattern.counterMeasures.isNotEmpty()) {
                append("\nPossible counter-moves:\n")
                pattern.counterMeasures.take(3).forEach { append("• $it\n") }
            }
            if (pattern.historicalAnalogs.isNotEmpty()) {
                append("\nHistorical echoes:\n")
                pattern.historicalAnalogs.take(2).forEach { append("• $it\n") }
            }
        }
    }
}
