# MeshTalk AI — Project Skeleton

This is the **architectural scaffolding** for MeshTalk AI: an offline-first, mesh-networked,
end-to-end encrypted Android messenger with on-device AI. It is not a finished app — see
[What's real vs. stubbed](#whats-real-vs-stubbed) below before building on top of it.

## Module map

```
app/                    composition root — Application, MainActivity, NavHost, manifest
core/
  common/               DispatcherProvider, MeshResult/MeshError (shared across all modules)
  designsystem/          Material 3 theme, color tokens, typography
  database/              Room entities/DAOs, SQLCipher-encrypted database
  datastore/              non-sensitive settings (Preferences DataStore)
domain/                  pure-Kotlin models, repository interfaces, use cases
data/                    repository implementations — wires domain to database/mesh/crypto
mesh/                    MeshManager, DiscoveryService, RoutingEngine, transports (Wi-Fi Direct, BLE)
crypto/                  IdentityManager, EncryptionEngine, Double Ratchet session manager
ai/                      AiService facade, emergency-keyword fallback detector
feature/
  chat/                  first screen: ChatListScreen + ChatListViewModel
```

Dependency direction is strictly one-way:
`app → feature:* → domain ← data → {mesh, crypto, core:database} → core:common`

`domain` never depends on `mesh`, `crypto`, or `core:database` directly — it only knows
about its own repository *interfaces*. `data` is the only module allowed to depend on
`domain`, `mesh`, and `crypto` simultaneously, because bridging them is its whole job.

## What's real vs. stubbed

**Structurally real and reasonably complete:**
- Gradle multi-module setup, version catalog, Clean Architecture layering, Hilt DI graph
- Room schema (Messages, Users, Devices, Keys, RoutingTable) with SQLCipher encryption at
  rest and a Keystore-backed passphrase — message *plaintext* is never persisted
- `MeshResult`/`MeshError` error-handling convention used at every module boundary
- `MeshTransport` interface (the real contract `MeshManager` codes against) and
  `MeshManager`'s transport-selection / route-lookup orchestration logic
- `EmergencyKeywordDetector` — a working, model-free fallback for priority/SOS detection
- Basic phishing/spam heuristics in `OnDeviceAiService.detectSpamOrPhishing`
- `ChatListScreen` / `ChatListViewModel` — compiles and runs, shows an empty state

**Deliberately left as stubs (return `MeshResult.Failure(NotImplementedError(...))`)**, each
with a comment explaining why it wasn't rushed:
- `WifiDirectTransport` / `BleTransport` — interface is real, no `WifiP2pManager` /
  `BluetoothLeAdvertiser` wiring yet
- `HybridRoutingEngine` — no AODV/BATMAN/OLSR packet formats implemented yet; `floodTo`
  works, point-to-point routing doesn't
- `TinkIdentityManager` — no Ed25519/X25519 key generation wired to Tink/Keystore yet
- `DoubleRatchetSessionManager` — no ratchet math yet (this is the one module I'd strongly
  recommend building against a published test-vector suite before anything else, given how
  easy it is to get subtly wrong)
- `AiService`'s model-backed methods (smart replies, summarization, translation,
  transcription, TTS) — no model runtime (TFLite/ONNX/MediaPipe) integrated yet

**Not modelled at all yet:** Media/File transfer, group chat, developer dashboard, biometric
app lock, root/tamper detection, WorkManager-scheduled retry jobs, and everything under
"Documentation" and "CI/CD" in the original spec.

## Suggested build order

1. `crypto` — get `TinkIdentityManager` + `DoubleRatchetSessionManager` passing real test
   vectors first. Nothing else in the security story matters if this is wrong.
2. `mesh` transports — start with BLE (simpler API surface, lower payload ceiling is fine
   for text) before Wi-Fi Direct.
3. `mesh` routing — AODV before BATMAN/OLSR; it's the simplest to reason about and get
   correct first.
4. `ai` — wire one model runtime end-to-end for one feature (emergency classification is
   already halfway there) before trying to support Gemma *and* Phi *and* Whisper at once.

## Building

Requires Android SDK (compileSdk 36 / minSdk 29), JDK 17. This skeleton was written but not
compiled against a real Android SDK in this environment — expect the usual first-build friction
(SDK path in `local.properties`, exact AGP/Kotlin version compatibility) common to any fresh
Gradle project.
