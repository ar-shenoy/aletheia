package com.realityfilter.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.realityfilter.app.domain.model.Pattern
import com.realityfilter.app.domain.model.PatternCategory
import com.realityfilter.app.domain.model.PatternPack
import com.realityfilter.app.domain.model.PatternPackManifest
import com.realityfilter.app.domain.model.Severity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class PatternPackRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pattern_packs", Context.MODE_PRIVATE)

    private val _packs = MutableStateFlow<List<PatternPack>>(emptyList())
    val packs: StateFlow<List<PatternPack>> = _packs.asStateFlow()

    suspend fun loadPacks() {
        val loaded = mutableListOf<PatternPack>()
        loaded.add(loadCoreSeedAsPack())
        try {
            val packFiles = context.assets.list("packs") ?: emptyArray()
            for (file in packFiles.filter { it.endsWith(".json") }) {
                try {
                    loaded.add(loadPackFromAsset("packs/$file"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (_: Exception) {}
        _packs.value = loaded.map { pack ->
            pack.copy(enabled = prefs.getBoolean("pack_${pack.id}", pack.enabled))
        }
    }

    fun setPackEnabled(packId: String, enabled: Boolean) {
        prefs.edit().putBoolean("pack_$packId", enabled).apply()
        _packs.value = _packs.value.map {
            if (it.id == packId) it.copy(enabled = enabled) else it
        }
    }

    fun enabledPatterns(): List<Pattern> {
        val seen = mutableSetOf<String>()
        val out = mutableListOf<Pattern>()
        for (pack in _packs.value.filter { it.enabled }) {
            for (p in pack.patterns) {
                if (seen.add(p.id)) out.add(p)
            }
        }
        return out
    }

    fun manifests(): List<PatternPackManifest> =
        _packs.value.map {
            PatternPackManifest(
                id = it.id,
                name = it.name,
                description = it.description,
                author = it.author,
                version = it.version,
                license = it.license,
                patternCount = it.patterns.size,
                enabled = it.enabled
            )
        }

    private fun loadCoreSeedAsPack(): PatternPack {
        val patterns = loadPatternsFromAsset("seed_patterns.json")
        return PatternPack(
            id = "core",
            name = "Core detectors",
            description = "Built-in cognitive biases, rhetoric, and persuasion patterns.",
            author = "Aletheia",
            version = 1,
            enabled = true,
            patterns = patterns
        )
    }

    private fun loadPackFromAsset(path: String): PatternPack {
        val json = readAsset(path)
        val obj = JSONObject(json)
        return PatternPack(
            id = obj.getString("id"),
            name = obj.getString("name"),
            description = obj.optString("description", ""),
            author = obj.optString("author", "Community"),
            version = obj.optInt("version", 1),
            license = obj.optString("license", "MIT"),
            enabled = true,
            patterns = parsePatterns(obj.getJSONArray("patterns"))
        )
    }

    private fun loadPatternsFromAsset(path: String): List<Pattern> =
        parsePatterns(JSONArray(readAsset(path)))

    private fun parsePatterns(array: JSONArray): List<Pattern> {
        val list = mutableListOf<Pattern>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            list.add(
                Pattern(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    category = PatternCategory.valueOf(o.getString("category")),
                    description = o.getString("description"),
                    detectionHints = toStringList(o.getJSONArray("detectionHints")),
                    examples = toStringList(o.optJSONArray("examples") ?: JSONArray()),
                    counterMeasures = toStringList(o.optJSONArray("counterMeasures") ?: JSONArray()),
                    historicalAnalogs = toStringList(o.optJSONArray("historicalAnalogs") ?: JSONArray()),
                    severity = Severity.valueOf(o.optString("severity", "MEDIUM")),
                    tags = toStringList(o.optJSONArray("tags") ?: JSONArray()),
                    version = o.optInt("version", 1)
                )
            )
        }
        return list
    }

    private fun toStringList(arr: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) list.add(arr.getString(i))
        return list
    }

    private fun readAsset(path: String): String {
        context.assets.open(path).use { input ->
            return BufferedReader(InputStreamReader(input)).readText()
        }
    }
}
