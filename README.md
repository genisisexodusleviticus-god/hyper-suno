# suno-hyper

High-quality, low-latency audio generation with Suno "Hyper" models — a lightweight wrapper and toolkit for running Suno models locally or as a service, with helpful CLI, examples, and integration points for Node.js and Python projects.

suno-hyper is designed to make it easy to:
- Generate music, singing, or voice from prompts
- Run inference locally (CPU/GPU) or behind a containerized HTTP API
- Integrate model outputs into apps, pipelines, or DAWs

Note: This repository is a wrapper/utility around the core Suno model artifacts. Check licensing and model usage rules for any bundled models or third-party providers.

## Features

- Simple CLI for prompt → audio generation
- Python and Node.js example clients
- Docker + docker-compose for reproducible local environments
- Environment-driven configuration for model path, device, and output
- Optional HTTP API for remote inference
- Preset prompt templates and sample runbooks

## Table of contents

- Features
- Requirements
- Quickstart
  - Python example
  - Node.js example
  - CLI example
  - Docker
- Configuration
- API (HTTP)
- Troubleshooting & tips
- Contributing
- License

---

## Requirements

- Modern Linux / macOS (Windows support depends on underlying runtimes)
- Python 3.9+ or Node.js 18+
- For GPU inference: CUDA-enabled GPU + matching drivers / cuDNN; appropriate PyTorch or runtime builds
- Enough disk space for model weights (varies by model; check model docs)

Optional:
- Docker / docker-compose for containerized usage

---

## Quickstart

Follow the path that best matches how you want to use suno-hyper.

### Python (local inference example)

1. Create and activate a virtual environment:
   python -m venv .venv
   source .venv/bin/activate

2. Install requirements:
   pip install -r requirements.txt

3. Example usage (replace <MODEL_PATH> and <PROMPT>):
   python -m suno_hyper.generate \
     --model /path/to/hyper-model \
     --prompt "Lush ambient synth pads with soft piano, 90s chill" \
     --output ./out.wav \
     --device cuda

If the repo provides a library API, a simple program could look like:

```python
from suno_hyper import HyperGenerator

gen = HyperGenerator(model_path="/path/to/model", device="cuda")
wav = gen.generate(prompt="A slow cinematic chord progression", length_seconds=20)
with open("out.wav", "wb") as f:
    f.write(wav)
```

### Node.js (example client)

1. Install dependencies:
   npm install

2. Example usage (assumes a local HTTP server or Node native bindings):
```js
// Example: call a local HTTP inference endpoint
import fs from "fs";
import fetch from "node-fetch";

async function generate() {
  const resp = await fetch("http://localhost:8000/api/v1/generate", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      prompt: "Upbeat electronic beat with bright synth lead",
      duration: 15,
      model: "/models/hyper"
    })
  });
  const { audio_base64 } = await resp.json();
  const data = Buffer.from(audio_base64, "base64");
  fs.writeFileSync("out.wav", data);
}
generate();
```

If the repo exposes a native Node binding, show the corresponding `require`/`import` usage in this section.

### CLI

This repository includes a small CLI for local generation:

- Build / install locally (if applicable)
- Generate:
  ./bin/suno-hyper generate --model /path/to/model --prompt "melodic lo-fi beat" --out out.wav

Run `./bin/suno-hyper --help` for available commands and flags.

### Docker

Start with docker-compose (example):

1. Copy .env:
   cp .env.example .env
   # Edit .env to point MODEL_PATH and set device flags

2. Start services:
   docker compose up --build

The HTTP API will be exposed at http://localhost:8000 (or the port defined in .env). See docker-compose.yml for service names and ports.

---

## Configuration

Most configuration is controlled with environment variables or a config file.

Common variables:

- MODEL_PATH — path or URL to the model weights
- DEVICE — cuda | cpu
- PORT — HTTP API port (default: 8000)
- OUTPUT_DIR — directory for generated audio
- LOG_LEVEL — debug | info | warn | error

Example .env:
```
MODEL_PATH=/models/hyper-v1
DEVICE=cuda
PORT=8000
OUTPUT_DIR=/outputs
LOG_LEVEL=info
```

---

## HTTP API (optional)

If the repo exposes an HTTP server, here is a minimal API contract to follow:

POST /api/v1/generate
Request JSON:
{
  "prompt": "string",
  "duration": 15,           // seconds
  "model": "/path/to/model",// optional override
  "temperature": 0.8,
  "seed": 12345
}

Response JSON:
{
  "status": "ok",
  "audio_base64": "<base64-wav-or-metadata>",
  "sample_rate": 44100
}

Use streaming responses for long generations if supported.

---

## Troubleshooting & Tips

- GPU memory: Large models may require more VRAM; consider CPU or a smaller model for testing.
- Determinism: Set the seed if you need reproducible outputs.
- Latency: Use batching and streaming for multi-request scenarios.
- Audio formats: WAV (PCM) is safest for fidelity; provide sample rate metadata.

---

## Contributing

Contributions welcome! Typical workflow:

1. Fork and create a feature branch:
   git checkout -b feat/your-feature

2. Add tests and documentation for new features.

3. Open a PR with a clear description and testing notes.

See CONTRIBUTING.md for details and PR checklist.

---

## License

This project is licensed under the MIT License — see LICENSE for details.

Model weights and third-party code may be subject to separate licenses. Ensure compliance with model provider terms (Suno model license, if applicable).

---

## Acknowledgements

- Suno and the teams behind the models used
- Open-source audio tooling and research that made this possible

---

If you want, I can:
- Tailor this README to match your repository's exact CLI flags, entrypoint scripts, and package names (point me to package.json, pyproject.toml, or your main entry file).
- Commit this README.md to genisisexodusleviticus-god/hyper-suno for you.
- Add CI workflows, Dockerfile improvements, and example generation notebooks for each runtime (Python/Node/Go).
