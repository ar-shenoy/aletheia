package com.realityfilter.app.data.repository

import android.content.Context
import com.realityfilter.app.data.local.PatternDao
import com.realityfilter.app.data.local.entities.PatternEntity
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.PatternCategory
import com.realityfilter.app.domain.model.Severity
import kotlinx.coroutines.flow.Flow
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
        // Always (re)load seed for v0.1 so library stays current after updates.
        // Later: only insert when table is empty.
        loadSeedPatternsFromAssets()
    }

    private suspend fun loadSeedPatternsFromAssets() {
        try {
            val inputStream = context.assets.open("seed_patterns.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()

            val jsonArray = JSONArray(jsonString)
            val entities = mutableListOf<PatternEntity>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val entity = PatternEntity(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = PatternCategory.valueOf(obj.getString("category")),
                    description = obj.getString("description"),
                    detectionHints = jsonArrayToList(obj.getJSONArray("detectionHints")),
                    examples = jsonArrayToList(obj.getJSONArray("examples")),
                    counterMeasures = jsonArrayToList(obj.optJSONArray("counterMeasures") ?: JSONArray()),
                    historicalAnalogs = jsonArrayToList(obj.optJSONArray("historicalAnalogs") ?: JSONArray()),
                    severity = Severity.valueOf(obj.getString("severity")),
                    tags = jsonArrayToList(obj.optJSONArray("tags") ?: JSONArray()),
                    version = obj.optInt("version", 1)
                )
                entities.add(entity)
            }

            patternDao.insertPatterns(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun jsonArrayToList(array: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

    private fun PatternEntity.toDomain() = Pattern(
        id = id,
        name = name,
        category = category,
        description = description,
        detectionHints = detectionHints,
        examples = examples,
        counterMeasures = counterMeasures,
        historicalAnalogs = historicalAnalogs,
        severity = severity,
        tags = tags,
        version = version
    )
}
