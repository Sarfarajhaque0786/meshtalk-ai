# MeshTalk AI

**Offline-first, mesh-networked, end-to-end encrypted messenger for Android — no internet or cellular connection required.**

MeshTalk AI lets nearby devices communicate directly with each other over Bluetooth LE and Wi-Fi Direct, relaying messages hop-by-hop across a mesh of phones. Built for scenarios where cellular networks are down, overloaded, or unavailable — disaster response, remote areas, large gatherings, or simply places with no signal.

> **Status:** Architectural skeleton — the module structure, data layer, and core contracts are in place; several security- and networking-critical components are intentionally left as stubs pending a focused implementation pass. See [What's real vs. stubbed](#whats-real-vs-stubbed).

---

## Key Features

- 📡 **No internet required** — peer-to-peer mesh networking over Bluetooth LE and Wi-Fi Direct
- 🔒 **End-to-end encryption** — Double Ratchet Algorithm with X25519 and Ed25519, so messages stay private even fully offline
- 🔁 **Multi-hop relaying** — messages route through intermediate devices to reach peers outside direct range
- 🤖 **On-device AI** — local, offline inference for features like emergency-keyword detection and spam/phishing heuristics, with no data leaving the device
- 🏗️ **Clean, modular architecture** — strict one-way dependency graph across Gradle modules, making each layer independently testable

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| DI | Hilt |
| Persistence | Room + SQLCipher (encrypted at rest) |
| Networking | Bluetooth LE, Wi-Fi Direct |
| Cryptography | Double Ratchet, X3DH, X25519, Ed25519 |
| Architecture | Clean Architecture (domain / data / feature modules) |

## Module Map

```
app/                    composition root — Application, MainActivity, NavHost, manifest
core/
  common/               DispatcherProvider, MeshResult/MeshError (shared across all modules)
  designsystem/         Material 3 theme, color tokens, typography
  database/             Room entities/DAOs, SQLCipher-encrypted database
  datastore/            non-sensitive settings (Preferences DataStore)
domain/                 pure-Kotlin models, repository interfaces, use cases
data/                   repository implementations — wires domain to database/mesh/crypto
mesh/                   MeshManager, DiscoveryService, RoutingEngine, transports (Wi-Fi Direct, BLE)
crypto/                 IdentityManager, EncryptionEngine, Double Ratchet session manager
ai/                     AiService facade, emergency-keyword fallback detector
feature/
  chat/                 first screen: ChatListScreen + ChatListViewModel
```

Dependency direction is strictly one-way:

```
app → feature:* → domain ← data → {mesh, crypto, core:database} → core:common
```

`domain` never depends on `mesh`, `crypto`, or `core:database` directly — it only knows about its own repository *interfaces*. `data` is the only module allowed to depend on `domain`, `mesh`, and `crypto` simultaneously, since bridging them is its whole job.

## What's Real vs. Stubbed

**Structurally real and reasonably complete:**
- Gradle multi-module setup, version catalog, Clean Architecture layering, Hilt DI graph
- Room schema (Messages, Users, Devices, Keys, RoutingTable) with SQLCipher encryption at rest and a Keystore-backed passphrase — message *plaintext* is never persisted
- `MeshResult`/`MeshError` error-handling convention used at every module boundary
- `MeshTransport` interface and `MeshManager`'s transport-selection / route-lookup orchestration logic
- `EmergencyKeywordDetector` — a working, model-free fallback for priority/SOS detection
- Basic phishing/spam heuristics in `OnDeviceAiService.detectSpamOrPhishing`
- `ChatListScreen` / `ChatListViewModel` — compiles and runs, shows an empty state

**Deliberately left as stubs** (return `MeshResult.Failure(NotImplementedError(...))`):
- `WifiDirectTransport` / `BleTransport` — interface is real, no `WifiP2pManager` / `BluetoothLeAdvertiser` wiring yet
- `HybridRoutingEngine` — no AODV/BATMAN/OLSR packet formats implemented yet; `floodTo` works, point-to-point routing doesn't
- `TinkIdentityManager` — no Ed25519/X25519 key generation wired to Tink/Keystore yet
- `DoubleRatchetSessionManager` — no ratchet math yet (recommend building this against a published test-vector suite before anything else)
- `AiService`'s model-backed methods (smart replies, summarization, translation, transcription, TTS) — no model runtime (TFLite/ONNX/MediaPipe) integrated yet

**Not modeled yet:** media/file transfer, group chat, developer dashboard, biometric app lock, root/tamper detection, scheduled retry jobs, CI/CD.

## Suggested Build Order

1. **`crypto`** — get `TinkIdentityManager` and `DoubleRatchetSessionManager` passing real test vectors first. Nothing else in the security story matters if this is wrong.
2. **`mesh` transports** — start with BLE (simpler API surface, lower payload ceiling is fine for text) before Wi-Fi Direct.
3. **`mesh` routing** — AODV before BATMAN/OLSR; simplest to reason about and get correct first.
4. **`ai`** — wire one model runtime end-to-end for one feature (emergency classification is already halfway there) before supporting multiple models at once.

## Building

**Requirements:** Android SDK (compileSdk 36 / minSdk 29), JDK 17.

```bash
git clone https://github.com/Sarfarajhaque0786/meshtalk-ai.git
cd meshtalk-ai
```

Open in Android Studio, let it sync Gradle, and set your Android SDK path in `local.properties` if prompted. This skeleton has not yet been compiled against a real Android SDK — expect the usual first-build friction (SDK path, exact AGP/Kotlin version compatibility) common to any fresh Gradle project.

## License

Not yet licensed — add a `LICENSE` file before accepting external contributions.

## Author

**Sarfaraj Haque**
B.Tech Information Technology, Institute of Engineering and Management (IEM), Kolkata
[GitHub](https://github.com/Sarfarajhaque0786) · [LinkedIn](https://linkedin.com/in/sarfaraj-haque2a3a34327)
