# 00 - Project Context

The mental model needed before reading any other page.

## What this repository actually is

The product is the **generator**, not the app. Evidence from `build-logic/convention`:

| Plugin | Size | Role |
| --- | --- | --- |
| `ScaffoldFeaturePlugin.kt` | ~15.2 KB | Generates a 4-module feature and wires it into the build |
| `ValidateSecretsPlugin.kt` | ~15.1 KB | `validateSecrets`, `scanApkForSecrets`, `hardeningReport` |
| `CreateNewAppPlugin.kt` | ~8.5 KB | Rebrands the template into a new sibling project |
| `AndroidLibraryNativeConventionPlugin.kt` | ~9.3 KB | NDK/CMake setup + secret injection |

That is roughly 48 KB of build-time logic — more than most `core` modules contain of runtime logic. The application code under `app/` and `feature/` behaves as a **living fixture**: CI generates a feature and a whole new app from it on every push.

## Three layers to keep separate in your head

1. **Generation layer** — `build-logic/convention`, Gradle tasks, text manipulation of `settings.gradle.kts` and `app/build.gradle.kts`.
2. **Runtime infrastructure** — 13 `core:*` modules: navigation, network, secrets, security, data, database, ui, config, analytics, permission, google-play, common.
3. **Product surface** — 8 features x 4 sub-modules, aggregated by `app`.

## Opinions the code enforces

- Every feature is **always** `domain` + `data` + `navigation` + `presentation`, with no exception for trivial features (`splash`, `detail` also carry 4 modules).
- Navigation is **feature-owned**: features contribute routes and `IScreenProvider` implementations via Hilt multibinding; nothing is registered centrally by hand.
- Screen state is standardized by `BaseViewModel<S, E>`: one `StateFlow` for state, one `Channel` for one-shot events.
- Secrets never live as plain Kotlin strings in release: they go through XOR-obfuscated byte arrays in native code.
- Build conventions are not optional: modules apply `composetemplate.*` plugins instead of configuring Android/Kotlin/Hilt themselves.

## Opinions the code does *not* enforce (worth knowing)

- There is no module-graph verification plugin. Layer boundaries are convention + review, not build-time assertion.
- Feature-to-feature isolation is not checked; `app` depends on everything.
- Presentation modules have no tests, so the ViewModel/state contract is unverified by CI.

## Scale snapshot

- ~48 Gradle modules (`:app`, 13 core, 32 feature, `:benchmark`, `:baselineprofile`).
- Composite build: `pluginManagement { includeBuild("build-logic") }`.
- `RepositoriesMode.FAIL_ON_PROJECT_REPOS` — modules cannot declare their own repositories.
- Languages: Kotlin, C++, CMake.

---

[Index](README.md) · [Next: 01 - Module Topology and Build System →](01-module-topology.md)
