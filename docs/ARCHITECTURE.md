# Architecture

## Goals

- Fully offline after model download
- Zero cloud dependency
- Transparent and inspectable logic
- Easy for community to add new pattern detectors

## Layers

### Domain
Pure Kotlin models and use cases. No Android dependencies.
- `Pattern`, `Observation`, `Claim`
- Future: `AnalyzeTextUseCase`, `GetPersonalBiasProfileUseCase`

### Data
- Room database (`reality_filter.db`)
- Entities + DAOs
- `PatternRepository` (currently loads seed JSON from assets)

### ML
- `PatternMatcher` (v0.1 = transparent heuristics)
- Next: on-device embedding search + small LLM for explanation generation

### UI
- Jetpack Compose
- Calm, high-signal design language
- Current: single Home screen for manual analysis

## Data Flow (v0.1)

1. App starts → Room DB initialized
2. Seed patterns loaded from `assets/seed_patterns.json` if empty
3. User pastes text → `PatternMatcher.analyze()`
4. Heuristic matching against detectionHints
5. Results shown as Observation cards

## Planned Evolution

- Replace / augment heuristics with on-device vector similarity
- Add camera + OCR pipeline
- Add conversation mode (opt-in mic)
- Personal bias mirror (local only)
- Modular pattern packs (community contributed)

## Privacy Rules (non-negotiable)

- No network calls for analysis
- No analytics SDKs
- No accounts
- User content never leaves the device unless explicitly exported by the user
