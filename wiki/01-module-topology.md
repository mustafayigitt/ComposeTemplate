# 01 - Module Topology and Build System

## Composite build setup

`settings.gradle.kts`:

- `pluginManagement { includeBuild("build-logic") }` — convention plugins are a separate build, not `buildSrc`.
- `RepositoriesMode.FAIL_ON_PROJECT_REPOS` — all repositories centralized.
- Foojay toolchain resolver `1.0.0` for JDK provisioning.
- Root project name: `ComposeTemplate`.

## Module inventory

| Group | Count | Notes |
| --- | --- | --- |
| `:app` | 1 | Composition root and dependency aggregator |
| `:core:*` | 13 | analytics, common, config, data, database, google-play, navigation, network, permission, secrets, security, ui (+ related) |
| `:feature:*:*` | 32 | 8 features x `domain`/`data`/`navigation`/`presentation` |
| `:benchmark`, `:baselineprofile` | 2 | Macrobenchmark + Baseline Profile generation |

## Convention plugins (17)

Applied by ID, e.g. `composetemplate.android.library`, `composetemplate.feature.presentation`.

- **Android base**: `android.application`, `android.library`, `android.library.native`, `android.application.compose`, `android.hilt`, `android.room`
- **Feature tiers**: `feature.domain`, `feature.data`, `feature.navigation`, `feature.presentation`
- **Tooling**: `test`, `static.analysis`, `create.new.app`, `validate.secrets`, `baselineprofile.generator`, plus shared `ProjectExtensions`

Each feature tier plugin encodes what that layer is allowed to depend on — this is where Clean Architecture is actually implemented, rather than in package naming.

## `app/build.gradle.kts`

- Plugins: `composetemplate.create.new.app`, `composetemplate.android.application`, `composetemplate.android.application.compose`, `composetemplate.android.hilt`, `composetemplate.test`, `androidx.baselineprofile`, `kotlin.serialization`.
- `namespace` and `applicationId` = `com.ytapps.composetemplate`; `versionCode`/`versionName` read from the version catalog.
- **Release signing** values come from the `secrets` extension with a `local.properties` fallback.
- `release`: `isMinifyEnabled = true`, `isShrinkResources = true`.
- Extra `benchmark` build type: `initWith(release)` + `benchmark-rules.pro`.
- `buildConfig = true` (needed for secret and flag plumbing).
- Declares **every** core and feature module dependency explicitly, plus Navigation3 libraries, Timber, and `profileinstaller`.

> **Warning:** Because `app/build.gradle.kts` lists all 32 feature modules by hand, it is mutated textually by `scaffoldFeature`. See [07 - Risks](07-risks-and-gaps.md).

## Version catalog highlights

| Area | Version |
| --- | --- |
| minSdk / targetSdk / compileSdk | 23 / 36 / 37 |
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

Note: the catalog carries both `converter-gson` and `kotlinx-serialization-core` — see [03 - Network](03-network-and-auth.md).

---

[← Previous: 00 - Project Context](00-project-context.md) · [Index](README.md) · [Next: 02 - Navigation and UI State →](02-navigation-and-ui-state.md)
