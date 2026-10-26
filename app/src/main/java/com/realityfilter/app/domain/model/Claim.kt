package com.realityfilter.app.domain.model

/**
 * A claim extracted from content that can be analyzed.
 */
data class Claim(
    val id: String,
    val text: String,
    val context: String? = null,
    val extractedAt: Long = System.currentTimeMillis()
)
