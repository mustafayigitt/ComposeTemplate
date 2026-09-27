# 01 - Module Topology and Build System

## Topology

`settings.gradle.kts` discovers directories containing `build.gradle.kts`; `:app` similarly aggregates discovered modules. The current tree contains the app, core libraries, four-layer feature verticals, benchmark tooling, and the included `build-logic` build.

Convention plugins enforce Android defaults, Compose, Hilt, testing, static analysis, module boundaries, generation, optional infrastructure, and performance wiring. `core:navigation` has no dependency on `core:data`; theme state is exposed through `IThemeManager`, not the navigation contract.

## Projection

`data.strategy.projection` supports `remote`, `offline-first`, `local`, and `minimal`. Required source rewrites demand exactly one match, and network-free outputs remove Retrofit, OkHttp, the kotlinx.serialization Retrofit converter, auth modules, and related residue. Database-free outputs remove Room and database scaffolding.

`offline-first` selects network/auth plus Room infrastructure. It deliberately does not generate a synchronization engine.

## Serialization

Navigation routes and Retrofit DTOs both use kotlinx.serialization. Retrofit is configured with `converter-kotlinx-serialization`; Gson is not part of the active network stack.

## Toolchain snapshot

- minSdk / targetSdk / compileSdk: 26 / 36 / 37
- Kotlin: 2.0.21
- AGP: 9.2.1
- Navigation3: 1.1.2
- Retrofit: 2.12.0
- Room: 2.8.4

---

[← Previous](00-project-context.md) · [Index](README.md) · [Next →](02-navigation-and-ui-state.md)
