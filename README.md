# Aletheia

**A local-first instrument for seeing the patterns that shape reality.**

Aletheia (ἀλήθεια) means *unconcealment* — truth as the act of revealing what is hidden.

This is an open-source Android app that runs entirely on your device.  
It detects persuasion structures, cognitive biases, missing base rates, rhetorical devices, and narrative techniques in the information you encounter — without sending anything to the cloud.

No accounts. No tracking. No servers. Just a private second brain for pattern literacy.

## Why Aletheia

Most tools either:
- Send your data to the cloud, or
- Are generic chatbots, or
- Focus only on productivity.

Aletheia is different. It is an **instrument**. It makes the operating system of human communication more legible to the individual using it.

## Core Principles

- **Local-first** — All analysis happens on-device
- **Privacy by design** — Nothing leaves the phone unless you export it
- **Calm & non-moralizing** — Surfaces patterns without lecturing
- **Open & modular** — Community can add new detectors
- **Zero recurring cost** — No backend, no GPU servers, no subscriptions

## Current Status (v0.1)

- [x] Clean architecture (domain / data / ui / ml)
- [x] Local Room database
- [x] Seed pattern library (cognitive biases, rhetorical devices, statistical fallacies, persuasion techniques)
- [x] Transparent heuristic matcher
- [x] Compose UI with calm dark/light theme
- [ ] On-device LLM / vision
- [ ] Live camera overlay
- [ ] Personal bias mirror
- [ ] Conversation mode

## Tech Stack

- Kotlin + Jetpack Compose
- Room (SQLite)
- On-device inference planned (MediaPipe / TensorFlow Lite → llama.cpp / ONNX)
- No remote databases, no analytics

## Getting Started

1. Clone the repository
2. Open in Android Studio (Ladybug or newer)
3. Let Gradle sync
4. Run on a device or emulator (API 26+)

```bash
./gradlew assembleDebug
```

## Contributing

The highest-leverage contributions right now:

- Expanding the seed pattern library with high-quality, well-documented detectors
- Improving the matching / scoring logic
- Designing the live overlay UI
- Preparing the path for on-device models

## License

MIT

---

*Named after the Greek concept of truth as disclosure.*
