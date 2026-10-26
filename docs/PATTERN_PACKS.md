# Pattern pack format

Community packs are JSON files loaded from `assets/packs/` or imported at runtime.

```json
{
  "id": "my_pack",
  "name": "My pack",
  "description": "What this pack detects",
  "author": "Your name",
  "version": 1,
  "license": "MIT",
  "patterns": [
    {
      "id": "unique_pattern_id",
      "name": "Human-readable name",
      "category": "COGNITIVE_BIAS",
      "description": "What the pattern is",
      "detectionHints": ["phrase or cue 1", "cue 2"],
      "examples": ["Example sentence"],
      "counterMeasures": ["How to push back"],
      "historicalAnalogs": [],
      "severity": "MEDIUM",
      "tags": ["tag1"]
    }
  ]
}
```

## Categories

`COGNITIVE_BIAS` | `RHETORICAL_DEVICE` | `PERSUASION_TECHNIQUE` | `STATISTICAL_FALLACY` | `NARRATIVE_STRUCTURE` | `POWER_DYNAMIC` | `MEDIA_TECHNIQUE` | `OTHER`

## Severity

`LOW` | `MEDIUM` | `HIGH` | `CRITICAL`

## Contribute

1. Add `your_pack.json` under `app/src/main/assets/packs/`
2. Open a PR with rationale and 2–3 examples
3. Prefer precision over recall in `detectionHints`

## Import at runtime

**Packs → Import pack from file** or **Import pack from URL**.
