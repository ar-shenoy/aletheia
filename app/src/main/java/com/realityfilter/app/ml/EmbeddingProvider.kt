package com.realityfilter.app.ml

import kotlin.math.sqrt

interface EmbeddingProvider {
    val dimension: Int
    fun embed(text: String): FloatArray
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var na = 0f
        var nb = 0f
        val n = minOf(a.size, b.size)
        for (i in 0 until n) {
            dot += a[i] * b[i]
            na += a[i] * a[i]
            nb += b[i] * b[i]
        }
        val denom = sqrt(na) * sqrt(nb)
        return if (denom < 1e-8f) 0f else (dot / denom).coerceIn(-1f, 1f)
    }
}

class HashEmbeddingProvider(
    override val dimension: Int = 256
) : EmbeddingProvider {

    override fun embed(text: String): FloatArray {
        val vec = FloatArray(dimension)
        val normed = text.lowercase().replace(Regex("\\s+"), " ").trim()
        if (normed.length < 3) return vec

        val padded = "  $normed  "
        for (i in 0 until padded.length - 2) {
            val gram = padded.substring(i, i + 3)
            val h = gram.hashCode()
            val idx = (h ushr 1) % dimension
            val sign = if (h and 1 == 0) 1f else -1f
            vec[idx] += sign
        }
        var sumSq = 0f
        for (v in vec) sumSq += v * v
        val norm = sqrt(sumSq)
        if (norm > 1e-8f) {
            for (i in vec.indices) vec[i] /= norm
        }
        return vec
    }
}

class OnDeviceModelEmbeddingProvider(
    private val modelAssetPath: String = "models/text_encoder.tflite"
) : EmbeddingProvider {
    override val dimension: Int = 384

    override fun embed(text: String): FloatArray {
        throw UnsupportedOperationException(
            "On-device model not bundled yet. Use HashEmbeddingProvider, " +
                "or place a TFLite encoder at assets/$modelAssetPath and implement this."
        )
    }
}
