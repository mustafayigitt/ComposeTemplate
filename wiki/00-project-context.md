# 00 - Project Context

The mental model needed before reading any other page.

## What this repository actually is

The product is the **generator**, not the app. Evidence from `build-logic/convention`:

| Plugin | Role |
| --- | --- |
| `ScaffoldFeaturePlugin.kt` | Generates a four-module feature from templates |
| `ValidateSecretsPlugin.kt` | `validateSecrets`, `scanApkForSecrets`, `hardeningReport` |
| `CreateNewAppPlugin.kt` | Rebrands the template into a new sibling project |
| `AndroidLibraryNativeConventionPlugin.kt` | NDK/CMake setup + secret injection |

Those four files carry more logic than most `core` modules contain of runtime logic. The application code under `app/` and `feature/` behaves as a **living fixture**: CI generates a feature and a whole new app from it on every push.

## Three layers to keep separate in your head

1. **Generation layer** — `build-logic/convention` and its Gradle tasks. It writes new module folders. It does **not** edit `settings.gradle.kts` or `app/build.gradle.kts`, because both derive their contents from whichever module folders exist on disk.
2. **Runtime infrastructure** — 12 `core:*` modules: analytics, common, config, data, database, google-play, navigation, network, permission, secrets, security, ui.
3. **Product surface** — 8 features x 4 sub-modules, aggregated by `app`.

## Opinions the code enforces

- Every feature is **always** `domain` + `data` + `navigation` + `presentation`, with no exception for trivial features (`splash`, `detail` also carry 4 modules).
- Navigation is **feature-owned**: features contribute routes and `IScreenProvider` implementations via Hilt multibinding; nothing is registered centrally by hand.
- Screen state is standardized by `BaseViewModel<S, E>`: one `StateFlow` for state, one `Channel` for one-shot events.
- Secrets never live as plain Kotlin strings in release: they go through XOR-obfuscated byte arrays in native code.
- Build conventions are not optional: modules apply `composetemplate.*` plugins instead of configuring Android/Kotlin/Hilt themselves.
- Optional modules stay deletable: `:app` may import symbols only from `core:common`, `core:navigation` and `core:ui`, and `checkAppModuleBoundary` fails the build when that rule is broken. Everything else reaches the app through DI multibindings.
- Modules are discovered from disk, so adding or removing one is a folder operation rather than a build-file edit.

## Opinions the code does *not* enforce (worth knowing)

- Feature-to-feature isolation is unchecked. The boundary task inspects `:app` only, so one feature importing another compiles happily.
- The boundary task reads Kotlin `import` lines. Coupling expressed in a build file is invisible to it — `:app` once declared `baselineProfile(project(":baselineprofile"))`, which blocked deletion of that module without a single import.
- `:app` still *depends* on every module at the Gradle level; what it may not do is *import* them. Removability comes from multibindings, not from a short dependency list.
- Presentation modules have no tests, so the ViewModel/state contract is unverified by CI.

## Scale snapshot

- 47 Gradle modules (`:app`, 12 core, 32 feature, `:benchmark`, `:baselineprofile`) — a count the discovery rule produces from the tree, not a fixed contract.
- 19 convention plugins in a composite build: `pluginManagement { includeBuild("build-logic") }`.
- `RepositoriesMode.FAIL_ON_PROJECT_REPOS` — modules cannot declare their own repositories.
- Languages: Kotlin, C++, CMake.

---

[Index](README.md) · [Next: 01 - Module Topology and Build System →](01-module-topology.md)
