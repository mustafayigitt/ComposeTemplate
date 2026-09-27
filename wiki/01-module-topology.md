# 01 - Module Topology and Build System

## Composite build and discovery

`settings.gradle.kts` includes `build-logic` through `pluginManagement`, centralizes repositories with `RepositoriesMode.FAIL_ON_PROJECT_REPOS`, and uses the Foojay resolver for JDK provisioning.

The settings script walks the repository and includes each directory that directly contains `build.gradle.kts`, excluding `build`, `build-logic`, `buildSrc`, `gradle`, and `src`. There is no hand-maintained `include(...)` inventory.

## Module inventory

| Group | Count | Notes |
| --- | ---: | --- |
| `:app` | 1 | Composition root and discovered dependency aggregator |
| `:core:*` | 12 | analytics, common, config, data, database, google-play, navigation, network, permission, secrets, security, ui |
| `:feature:*:*` | 32 | 8 features × 4 layers |
| `:benchmark`, `:baselineprofile` | 2 | Macrobenchmark and Baseline Profile tooling |

Total: **47 Gradle modules**. The 12 core and 32 feature submodules are **44 Android libraries**. Counts describe the current tree; discovery makes them an outcome rather than a fixed include list.

## Convention plugins (21)

Registered IDs are grouped below:

- **Android base**: `android.application`, `android.application.compose`, `android.library`, `android.library.compose`, `android.library.native`, `android.hilt`, `android.room`
- **Feature layers**: `feature.domain`, `feature.data`, `feature.navigation`, `feature.presentation`
- **Generation**: `create.new.app`, `data.strategy.projection`, `scaffold.feature`
- **Quality/operations**: `test`, `static.analysis`, `validate.secrets`, `baseline.profile.generator`, `app.boundary`, `module.boundary`, `perf`

`ProjectExtensions` contains shared helpers but is not a registered plugin.

## Boundary enforcement

- `checkAppModuleBoundary` scans app imports.
- `checkModuleBoundary` scans library imports according to the module’s Gradle path.
- `checkProjectDependencyBoundary` scans literal `project(":…")` and `project(path = ":…")` dependencies.
- Dynamic paths remain allowed because `:app` intentionally wires discovered projects.

These checks protect folder-level removability. Optional consumers receive implementations through multibindings rather than direct imports.

## `app/build.gradle.kts`

The application applies generation, projection, Android application, Compose, Hilt, test, performance, and serialization plugins, including `composetemplate.data.strategy.projection`.

Other behavior:

- namespace/application ID start as `com.ytapps.composetemplate`
- version code/name come from the catalog
- release minification and resource shrinking are enabled
- signing is secret-backed only when that subsystem is retained
- a benchmark build type derives from release
- core and feature project dependencies are derived from discovered projects

## Strategy projection at the build level

`data.strategy.projection` removes unselected module paths and related catalog entries, conventions, scaffolding, ProGuard text, navigation source, and secret/signing infrastructure. It keeps the four-layer feature shape unchanged.

Database-backed outputs retain optional Room starter generation through `-PwithDatabase=true`. Database-free outputs contain no Room scaffolding flag, path, or identifier.

## Toolchain snapshot

| Area | Version |
| --- | --- |
| minSdk / targetSdk / compileSdk | 26 / 36 / 37 |
| NDK | 27.0.12077973 |
| Kotlin | 2.0.21 |
| AGP | 9.2.1 |
| KSP | 2.0.21-1.0.28 |
| Compose BOM | 2026.05.01 |
| Navigation3 | 1.1.2 |
| Hilt | 2.59.2 |
| Retrofit / OkHttp | 2.12.0 / 4.12.0 |
| Room | 2.8.4 |
| DataStore | 1.2.1 |
| Coil | 3.4.0 |

The catalog currently includes Gson for Retrofit and kotlinx.serialization for routes; this split is documented in [03 - Network and Auth](03-network-and-auth.md).

---

[← Previous: 00 - Project Context](00-project-context.md) · [Index](README.md) · [Next: 02 - Navigation and UI State →](02-navigation-and-ui-state.md)
