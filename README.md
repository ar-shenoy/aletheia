# Aletheia

Local-first pattern recognition for human communication. No accounts. No servers.

## v0.6

| Tab | Capability |
|-----|------------|
| Analyze | Hybrid matching |
| Patterns | Library + detail |
| Mirror | Bias profile + bookmarks |
| Talk | Continuous sessions + **past session history** |
| Live | Camera OCR overlay (persists hits) |
| Packs | Toggle + **import file/URL** |

Also: 16 seed detectors, safer seed migration, adaptive icon, ProGuard rules, matcher unit tests, pack contribution docs.

### Optional neural embeddings

Place a TFLite encoder at `app/src/main/assets/models/text_encoder.tflite`. Without it, hash embeddings are used.

### Build

```bash
git clone https://github.com/ar-shenoy/aletheia.git
```

Open in Android Studio (API 26+) → sync → run.

```bash
./gradlew test
./gradlew :app:assembleDebug
```

## License

MIT
