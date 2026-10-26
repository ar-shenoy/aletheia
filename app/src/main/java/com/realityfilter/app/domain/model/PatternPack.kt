package com.realityfilter.app.domain.model

data class PatternPack(
    val id: String,
    val name: String,
    val description: String,
    val author: String = "Aletheia",
    val version: Int = 1,
    val license: String = "MIT",
    val enabled: Boolean = true,
    val patterns: List<Pattern> = emptyList()
)

data class PatternPackManifest(
    val id: String,
    val name: String,
    val description: String,
    val author: String = "Aletheia",
    val version: Int = 1,
    val license: String = "MIT",
    val patternCount: Int = 0,
    val enabled: Boolean = true
)
