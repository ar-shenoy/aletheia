package com.realityfilter.app.data.repository

import android.content.Context
import com.realityfilter.app.data.local.PatternDao
import com.realityfilter.app.data.local.entities.PatternEntity
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.PatternCategory
import com.realityfilter.app.domain.model.Severity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class PatternRepository(
    private val patternDao: PatternDao,
    private val context: Context
) {

    fun getAllPatterns(): Flow<List<Pattern>> =
        patternDao.getAllPatterns().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun getPatternById(id: String): Pattern? =
        patternDao.getPatternById(id)?.toDomain()

    suspend fun seedPatternsIfEmpty() {
        val existing = patternDao.getAllPatterns().first()
        if (existing.isEmpty()) {
            loadSeedPatternsFromAssets(replaceAll = true)
        } else {
            loadSeedPatternsFromAssets(replaceAll = false)
        }
    }

    private suspend fun loadSeedPatternsFromAssets(replaceAll: Boolean) {
        try {
            val jsonString = context.assets.open("seed_patterns.json").use { input ->
                BufferedReader(InputStreamReader(input)).readText()
            }
            val jsonArray = JSONArray(jsonString)
            val entities = mutableListOf<PatternEntity>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                entities.add(
                    PatternEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        category = PatternCategory.valueOf(obj.getString("category")),
                        description = obj.getString("description"),
                        detectionHints = jsonArrayToList(obj.getJSONArray("detectionHints")),
                        examples = jsonArrayToList(obj.optJSONArray("examples") ?: JSONArray()),
                        counterMeasures = jsonArrayToList(obj.optJSONArray("counterMeasures") ?: JSONArray()),
                        historicalAnalogs = jsonArrayToList(obj.optJSONArray("historicalAnalogs") ?: JSONArray()),
                        severity = Severity.valueOf(obj.optString("severity", "MEDIUM")),
                        tags = jsonArrayToList(obj.optJSONArray("tags") ?: JSONArray()),
                        version = obj.optInt("version", 1)
                    )
                )
            }
            if (replaceAll) {
                patternDao.insertPatterns(entities)
            } else {
                val current = patternDao.getAllPatterns().first().associateBy { it.id }
                val toUpsert = entities.filter { e ->
                    val old = current[e.id]
                    old == null || e.version > old.version
                }
                if (toUpsert.isNotEmpty()) patternDao.insertPatterns(toUpsert)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun jsonArrayToList(array: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) list.add(array.getString(i))
        return list
    }

    private fun PatternEntity.toDomain() = Pattern(
        id = id, name = name, category = category, description = description,
        detectionHints = detectionHints, examples = examples,
        counterMeasures = counterMeasures, historicalAnalogs = historicalAnalogs,
        severity = severity, tags = tags, version = version
    )
}
