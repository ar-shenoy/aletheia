# Aletheia

**Local-first pattern recognition for human communication.**

No accounts. No servers. On-device analysis.

## v0.5 features

| Tab | Capability |
|-----|------------|
| Analyze | Hybrid matching (keywords + embeddings) |
| Patterns | Full library + detail |
| Mirror | Bias profile + bookmarks |
| Talk | Continuous multi-turn sessions (Room-persisted) |
| Live | Camera OCR overlay |
| Packs | Toggle packs + **import JSON from file** |

### Matching
- `HashEmbeddingProvider` always available
- `OnDeviceModelEmbeddingProvider` loads `assets/models/text_encoder.tflite` when present (TFLite)
- `EmbeddingFactory` picks the best available backend

### Seed library
16 built-in detectors + modular packs under `assets/packs/`.

### Build

```bash
git clone https://github.com/ar-shenoy/aletheia.git
cd aletheia
# Android Studio, or:
# place gradle-wrapper.jar then: ./gradlew :app:assembleDebug
```

Optional encoder: `scripts/download_encoder.sh` then drop a `.tflite` into `app/src/main/assets/models/text_encoder.tflite`.

## License

MIT
