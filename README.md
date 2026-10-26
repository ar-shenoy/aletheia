# Aletheia

**A local-first instrument for seeing the patterns that shape reality.**

Fully on-device Android app. No accounts. No tracking. No servers.

## Features (v0.4)

| Tab | Capability |
|-----|------------|
| **Analyze** | Paste / share / image OCR → hybrid pattern matching |
| **Patterns** | Library + detail |
| **Mirror** | Personal bias profile + bookmarks |
| **Talk** | Continuous multi-turn speech sessions |
| **Live** | Camera overlay: OCR + live pattern chips |
| **Packs** | Enable/disable modular detector packs |

### Stronger matching
Hybrid scorer: keyword hints + local hash embeddings (swap-in path for TF-Lite encoders).

### Modular packs
Core seed + community JSON under `assets/packs/`.

## Build

```bash
git clone https://github.com/ar-shenoy/aletheia.git
```

Open in Android Studio → Gradle sync → Run (API 26+).

## License

MIT
