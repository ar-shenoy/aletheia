package com.realityfilter.app.ml

import com.realityfilter.app.domain.model.Observation
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.SourceType
import java.util.UUID

class HybridPatternMatcher(
    private val embeddings: EmbeddingProvider = HashEmbeddingProvider(),
    private val keywordWeight: Float = 0.55f,
    private val embeddingWeight: Float = 0.45f,
    private val minConfidence: Float = 0.32f
) {
    private val keywordMatcher = PatternMatcher()
    private var patternVectors: Map<String, FloatArray> = emptyMap()

    fun prepare(patterns: List<Pattern>) {
        patternVectors = patterns.associate { p ->
            val corpus = buildString {
                append(p.name).append(' ')
                append(p.description).append(' ')
                p.detectionHints.forEach { append(it).append(' ') }
                p.examples.take(2).forEach { append(it).append(' ') }
            }
            p.id to embeddings.embed(corpus)
        }
    }

    fun analyze(
        text: String,
        patterns: List<Pattern>,
        sourceType: SourceType = SourceType.MANUAL_TEXT
    ): List<Observation> {
        if (text.isBlank()) return emptyList()
        if (patternVectors.isEmpty()) prepare(patterns)

        val keywordHits = keywordMatcher.analyze(text, patterns).associateBy { it.patternId }
        val textVec = embeddings.embed(text)
        val results = mutableListOf<Observation>()

        for (pattern in patterns) {
            val kw = keywordHits[pattern.id]
            val kwScore = kw?.confidence ?: 0f
            val pVec = patternVectors[pattern.id]
            val embScore = if (pVec != null) {
                val cos = embeddings.cosineSimilarity(textVec, pVec)
                ((cos + 1f) / 2f).coerceIn(0f, 1f)
            } else 0f

            val effectiveEmb = if (embScore >= 0.58f) embScore else embScore * 0.3f
            val blended = keywordWeight * kwScore + embeddingWeight * effectiveEmb
            if (blended < minConfidence) continue

            val matchedHints = kw?.matchedHints?.toMutableList() ?: mutableListOf()
            if (effectiveEmb >= 0.58f && matchedHints.none { it.startsWith("~") }) {
                matchedHints.add("~ embedding similarity ${(effectiveEmb * 100).toInt()}%")
            }

            results.add(
                Observation(
                    id = UUID.randomUUID().toString(),
                    patternId = pattern.id,
                    sourceType = sourceType,
                    sourceText = text.take(500),
                    confidence = blended.coerceIn(0.2f, 0.95f),
                    explanation = buildExplanation(pattern, kwScore, effectiveEmb, matchedHints),
                    matchedHints = matchedHints
                )
            )
        }
        return results.sortedByDescending { it.confidence }
    }

    private fun buildExplanation(
        pattern: Pattern,
        kwScore: Float,
        embScore: Float,
        hints: List<String>
    ): String {
        val parts = mutableListOf<String>()
        parts.add("Detected: ${pattern.name}.")
        if (kwScore > 0.2f) parts.add("Keyword/hint score ${(kwScore * 100).toInt()}%.")
        if (embScore >= 0.5f) parts.add("Semantic similarity ${(embScore * 100).toInt()}%.")
        if (hints.isNotEmpty()) parts.add("Signals: ${hints.take(3).joinToString("; ")}.")
        return parts.joinToString(" ")
    }
}
