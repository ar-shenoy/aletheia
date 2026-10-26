package com.realityfilter.app.ml

import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.PatternCategory
import com.realityfilter.app.domain.model.Severity
import org.junit.Assert.assertTrue
import org.junit.Test

class PatternMatcherTest {

    private val falseDichotomy = Pattern(
        id = "false_dichotomy",
        name = "False Dichotomy",
        category = PatternCategory.RHETORICAL_DEVICE,
        description = "Only two options presented",
        detectionHints = listOf(
            "either X or Y framing",
            "you are either with us or against us",
            "forced binary"
        ),
        examples = listOf("You are either with us or against us."),
        severity = Severity.HIGH
    )

    @Test
    fun detectsStrongPhrase() {
        val matcher = PatternMatcher()
        val text = "In this conflict you are either with us or against us — choose."
        val results = matcher.analyze(text, listOf(falseDichotomy))
        assertTrue(results.any { it.patternId == "false_dichotomy" })
        assertTrue(results.first().confidence > 0.3f)
    }

    @Test
    fun emptyTextReturnsEmpty() {
        val matcher = PatternMatcher()
        assertTrue(matcher.analyze("  ", listOf(falseDichotomy)).isEmpty())
    }

    @Test
    fun hashEmbeddingIsNormalized() {
        val emb = HashEmbeddingProvider(64)
        val v = emb.embed("availability bias and vivid examples")
        val norm = kotlin.math.sqrt(v.map { it * it }.sum())
        assertTrue(norm > 0.99f && norm < 1.01f)
    }
}
