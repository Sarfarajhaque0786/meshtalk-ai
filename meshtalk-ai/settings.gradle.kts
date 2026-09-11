pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MeshTalkAI"

// ---------------------------------------------------------------------------------------------
// Module map (mirrors Clean Architecture layering described in /docs/architecture.md):
//
//   app                     -> composition root, navigation graph, DI wiring
//   core:common             -> shared utilities, coroutine dispatchers, Result wrapper
//   core:designsystem       -> Material 3 theme, typography, color tokens
//   core:database           -> Room (encrypted) - Messages, Users, Keys, Devices, Media, Routing
//   core:datastore          -> Proto DataStore - user settings / preferences
//   domain                  -> pure Kotlin - models, repository interfaces, use cases
//   data                    -> repository implementations, bridges domain <-> core/mesh/crypto/ai
//   mesh                    -> Mesh Manager, Discovery Service, Routing Engine, transports
//   crypto                  -> Identity Manager, Encryption Engine, Double Ratchet, Key Storage
//   ai                      -> on-device AI Service (smart replies, emergency detection, etc.)
//   feature:chat            -> presentation layer (Compose UI + ViewModel) for chat
// ---------------------------------------------------------------------------------------------

include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":domain")
include(":data")
include(":mesh")
include(":crypto")
include(":ai")
include(":feature:chat")
