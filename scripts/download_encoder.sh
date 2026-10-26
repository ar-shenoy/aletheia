#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/app/src/main/assets/models"
mkdir -p "$OUT"
echo "Place a TFLite sentence encoder at: $OUT/text_encoder.tflite"
echo "Until then, HashEmbeddingProvider is used automatically."
touch "$OUT/.gitkeep"
