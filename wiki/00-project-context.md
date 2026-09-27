# 00 - Project Context

The mental model needed before reading any other page.

## What this repository is

The product is the **generator**, not a finished application. The source app and feature tree are a living fixture that exercises the same conventions and structures emitted for consumers.

Key build-logic entry points:

| Plugin | Role |
| --- | --- |
| `create.new.app` | Copies, rebrands, projects, cleans, and validates a consumer project |
| `data.strategy.projection` | Applies `remote`, `offline-first`, `local`, or `minimal` infrastructure |
| `scaffold.feature` | Creates the fixed four-module feature shape |
| `validate.secrets` | Validates inputs and scans release artifacts |
| `android.library.native` | Configures NDK/CMake secret injection when retained |

## Three layers to keep separate

1. **Generation layer** — `build-logic/convention` tasks and plugins write or project files. They do not maintain a static module list because the build discovers module folders.
2. **Runtime infrastructure** — 12 `core:*` groups: analytics, common, config, data, database, google-play, navigation, network, permission, secrets, security, and ui.
3. **Product surface** — 8 features, each with `data`, `domain`, `navigation`, and `presentation`, aggregated by `:app`.

## Data-strategy contract

| Strategy | Network/auth | Room | Generated start flow |
| --- | ---: | ---: | --- |
| `remote` | Yes | No | Onboarding → Login/Home |
| `offline-first` | Yes | Yes | Onboarding → Login/Home |
| `local` | No | Yes | Onboarding → Home |
| `minimal` | No | No | Onboarding → Home |

`remote` is the default. `withSecrets` is a strict Boolean and is meaningful for network-backed strategies. Local and minimal remove secrets/security automatically.

The name `offline-first` describes the selected topology only: the generated project contains both network/auth and Room foundations. Synchronization, queues, conflict handling, cache policy, and reconciliation are product work and are not generated.

## Opinions enforced by code

- Every retained feature has exactly four layers: `data`, `domain`, `navigation`, and `presentation`.
- Features publish navigation contracts and contribute screens through Hilt multibindings.
- Screen state follows `BaseViewModel<S, E>` with state and one-shot events.
- Build conventions are applied through `composetemplate.*` plugins.
- Modules are discovered from directories containing `build.gradle.kts`; adding or removing a module is primarily a folder operation.
- `checkAppModuleBoundary`, `checkModuleBoundary`, and `checkProjectDependencyBoundary` enforce source/build dependency boundaries.
- Optional integrations use DI multibindings where there is a real consumer.
- App-shell connectivity monitoring and the global offline banner are intentionally absent.

## Boundary model

- `:app` may import only `core:common`, `core:navigation`, and `core:ui`.
- Always-present core modules (`common`, `navigation`, `ui`, `data`) may name only one another.
- Other core modules may depend on core modules but not features.
- Features may reference other features only through navigation contracts.
- Dynamic project paths remain allowed so discovery can wire the module folders that exist.

`:app` still aggregates discovered modules at the Gradle level. Deletion safety comes from restricted imports, dependency rules, and multibindings—not from a short dependency list.

## Scale snapshot

- 47 Gradle modules: `:app`, 12 core, 32 feature submodules, `:benchmark`, and `:baselineprofile`.
- 44 Android library modules: 12 core plus 32 feature submodules.
- 21 registered convention plugins in the included `build-logic` build.
- Kotlin, C++, and CMake sources.
- Central repositories enforced with `RepositoriesMode.FAIL_ON_PROJECT_REPOS`.

## What the template does not promise

A generated consumer is not a production-ready product by itself. It still needs product-specific requirements, backend contracts, domain logic, branding, signing, migrations, operational policy, and runtime/UI validation. Native XOR secret handling raises extraction cost but is not secure client-side secret storage.

Repository policy such as branch protection and required checks should be verified and enforced in GitHub settings; documentation does not treat a transient setting as an architectural guarantee.

---

[Index](README.md) · [Next: 01 - Module Topology and Build System →](01-module-topology.md)
