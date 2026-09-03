# 01 - Module Topology and Build System

## Composite build setup

`settings.gradle.kts`:

- `pluginManagement { includeBuild("build-logic") }` — convention plugins are a separate build, not `buildSrc`.
- `RepositoriesMode.FAIL_ON_PROJECT_REPOS` — all repositories centralized.
- Foojay toolchain resolver `1.0.0` for JDK provisioning.
- Root project name: `ComposeTemplate`.
- Modules are **discovered from disk**. The script walks the tree and includes every directory that directly contains a `build.gradle.kts`, skipping `build`, `build-logic`, `buildSrc`, `gradle` and `src`. There is no hand-maintained `include(...)` list, which is why plugging a module out is a single folder delete.

## Module inventory

| Group | Count | Notes |
| --- | --- | --- |
| `:app` | 1 | Composition root and dependency aggregator |
| `:core:*` | 12 | analytics, common, config, data, database, google-play, navigation, network, permission, secrets, security, ui |
| `:feature:*:*` | 32 | 8 features x `domain`/`data`/`navigation`/`presentation` |
| `:benchmark`, `:baselineprofile` | 2 | Macrobenchmark + Baseline Profile generation |

These counts describe what the tree contains today, not a contract. Because modules are discovered, adding or deleting a module folder changes the inventory with no build-file edit anywhere.

## Convention plugins (19)

Applied by ID, e.g. `composetemplate.android.library`, `composetemplate.feature.presentation`.

- **Android base**: `android.application`, `android.application.compose`, `android.library`, `android.library.compose`, `android.library.native`, `android.hilt`, `android.room`
- **Feature tiers**: `feature.domain`, `feature.data`, `feature.navigation`, `feature.presentation`
- **Tooling**: `test`, `static.analysis`, `create.new.app`, `scaffold.feature`, `validate.secrets`, `baseline.profile.generator`, `app.boundary`, `perf`, plus the shared `ProjectExtensions` helpers (not a plugin)

Each feature tier plugin encodes what that layer is allowed to depend on — this is where Clean Architecture is actually implemented, rather than in package naming.

Two of these exist to protect the plug-out property rather than to configure a module:

- **`app.boundary`** registers `checkAppModuleBoundary`, which fails the build when `:app` imports a symbol from any module other than `core:common`, `core:navigation` or `core:ui`.
- **`perf`** applies the baseline profile plugin and its dependencies **only when `:baselineprofile` is part of the build**, so deleting the folder is enough to remove performance tooling.

## `app/build.gradle.kts`

- Plugins: `composetemplate.create.new.app`, `composetemplate.android.application`, `composetemplate.perf`, `composetemplate.android.application.compose`, `composetemplate.android.hilt`, `composetemplate.test`, `kotlin.serialization`.
- `namespace` and `applicationId` = `com.ytapps.composetemplate`; `versionCode`/`versionName` read from the version catalog.
- **Release signing** values come from the `secrets` extension with a `local.properties` fallback.
- `release`: `isMinifyEnabled = true`, `isShrinkResources = true`.
- Extra `benchmark` build type: `initWith(release)` + `benchmark-rules.pro`. It stays even though `:benchmark` itself is removable, because the build type is what a macrobenchmark run targets and keeping it costs nothing.
- `buildConfig = true` (needed for secret and flag plumbing).
- Core and feature module dependencies are **derived from the discovered projects**, not listed by hand. Only libraries `:app` uses directly are declared explicitly, such as the Navigation3 libraries and Timber.

> **Note:** `:app` may import symbols only from `core:common`, `core:navigation` and `core:ui`. Every other module reaches the app through DI multibindings instead of imports, and `checkAppModuleBoundary` fails the build if that rule is broken. See [06 - Quality, Tests and CI](06-quality-tests-ci.md) and [07 - Risks](07-risks-and-gaps.md).

One class of coupling this check cannot see is build-file coupling: `:app` once declared `baselineProfile(project(":baselineprofile"))` in its own script, which broke deletion of that module without a single Kotlin import. That is what `composetemplate.perf` fixed.

## Version catalog highlights

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
| Detekt / ktlint plugin | 1.23.8 / 14.2.0 |
| Test stack | JUnit 4.13.2, MockK 1.14.11, Truth 1.4.5, coroutines-test 1.11.0 |

Every module reads `minSdk`, `targetSdk` and `compileSdk` from this catalog through a convention plugin — no module declares them itself, so the baseline moves in one edit. The reasoning behind the Android 8.0 baseline is recorded in [07 - Risks](07-risks-and-gaps.md#baseline-decision-log).

Note: the catalog carries both `converter-gson` and `kotlinx-serialization-core` — see [03 - Network](03-network-and-auth.md).

---

[← Previous: 00 - Project Context](00-project-context.md) · [Index](README.md) · [Next: 02 - Navigation and UI State →](02-navigation-and-ui-state.md)
