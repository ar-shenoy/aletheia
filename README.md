# Aletheia

**A local-first instrument for seeing the patterns that shape reality.**

Aletheia (ἀλήθεια) means *unconcealment* — truth as the act of revealing what is hidden.

Open-source Android app. Fully on-device. No accounts. No tracking. No servers.

## Features (v0.2)

- **Analyze** — paste text or share from other apps; run local pattern detection
- **Image OCR** — pick an image, extract text with ML Kit (on-device), then analyze
- **Pattern Library** — browse detectors by category with severity badges
- **Personal Bias Mirror** — sees which patterns appear most often in *your* analyses (local only)
- **Persistence** — bookmarks + analysis sessions stored in Room (SQLite)
- **Share sheet** — appears in Android share menu for `text/plain`

## Tech

- Kotlin + Jetpack Compose + Material 3
- Room (local SQLite)
- ML Kit Text Recognition (on-device OCR)
- Transparent heuristic `PatternMatcher` (ready for on-device embeddings later)

## Build

1. Clone: `git clone https://github.com/ar-shenoy/aletheia.git`
2. Open in Android Studio (Ladybug+)
3. Let Gradle sync
4. Run on device/emulator (API 26+)

```bash
./gradlew assembleDebug
```

> Note: First open may require generating default launcher icons in Android Studio (New → Image Asset) if `mipmap` resources are missing.

## Privacy

- All analysis runs on-device
- Nothing is sent to a server
- Bias Mirror is built only from content you choose to analyze

## License

MIT

---

*Named after the Greek concept of truth as disclosure.*
