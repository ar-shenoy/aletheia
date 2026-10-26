# Aletheia

**A local-first instrument for seeing the patterns that shape reality.**

Aletheia (ἀλήθεια) means *unconcealment* — truth as disclosure.

Fully on-device Android app. No accounts. No tracking. No servers.

## Features (v0.3)

| Tab | What it does |
|-----|----------------|
| **Analyze** | Paste text, share from other apps, or OCR an image → local pattern detection |
| **Patterns** | Browse the detector library by category; open full detail (signals, examples, counter-moves) |
| **Mirror** | Personal bias profile from *your* analyses + bookmarked signals |
| **Talk** | Opt-in conversation mode: on-device speech → transcript → analyze |

Also:
- Room persistence (sessions, observations, bookmarks)
- Share sheet (`text/plain`)
- ML Kit on-device OCR
- Transparent heuristic matcher (seed detectors)

## Build

```bash
git clone https://github.com/ar-shenoy/aletheia.git
```

Open in Android Studio (Ladybug+) → Gradle sync → Run (API 26+).

Generate launcher icons if missing: **File → New → Image Asset**.

## Privacy

Everything runs on-device. Bias Mirror is built only from content you choose to analyze.

## Roadmap (next)

- Stronger matching (embeddings / small on-device LLM)
- Live camera overlay annotations
- Modular community pattern packs
- Continuous conversation listening with clearer session boundaries

## License

MIT
