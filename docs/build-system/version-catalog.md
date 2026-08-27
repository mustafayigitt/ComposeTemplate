# Version Catalog

ComposeTemplate uses Gradle's version catalog to centralize dependency and plugin versions.

The catalog lives in `gradle/libs.versions.toml` and acts as the project's dependency governance layer.

## Problem

Without centralized dependency management, Android projects drift quickly:

- one module uses a newer lifecycle version,
- another uses a different Compose dependency,
- KSP and Kotlin become incompatible,
- plugin versions are hardcoded in multiple places,
- dependency upgrades become risky and hard to review.

A version catalog makes dependency decisions visible in one place.

## What the catalog controls

ComposeTemplate's catalog includes:

- SDK versions,
- AndroidX versions,
- Compose BOM,
- Navigation3,
- DataStore,
- Room,
- Retrofit and OkHttp,
- Hilt,
- Kotlin and KSP,
- static analysis tools,
- testing libraries,
- Macrobenchmark and Baseline Profile dependencies,
- Google Play libraries,
- Coil,
- build logic dependencies,
- Gradle plugin aliases.

## Version aliases

Version aliases define reusable version numbers:

```toml
[versions]
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
android-gradle-plugin = "9.2.1"
androidx-compose-bom = "2026.05.01"
```

These versions are then referenced by libraries and plugins.

## Library aliases

Library aliases define dependencies used by modules and convention plugins:

```toml
[libraries]
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
```

This keeps dependency coordinates out of individual module files.

## Plugin aliases

Plugin aliases centralize Gradle plugin versions:

```toml
[plugins]
android-application = { id = "com.android.application", version.ref = "android-gradle-plugin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

Convention plugins can then apply and configure plugins consistently.

## Compose BOM

Compose dependencies are managed through the Compose BOM. The BOM lets Compose libraries align to a compatible set without repeating versions on every Compose artifact.

This reduces mismatch risk across UI modules.

## Kotlin and KSP compatibility

KSP is tied closely to Kotlin versions. When upgrading Kotlin, verify that the KSP version is compatible.

A safe upgrade path is:

1. update Kotlin,
2. update KSP to the matching compatibility line,
3. run Gradle sync,
4. run unit tests,
5. run generated feature compile checks,
6. run release build checks.

## Dependency upgrade checklist

- [ ] update the version catalog, not scattered module files.
- [ ] verify Kotlin/KSP compatibility.
- [ ] verify AGP/Gradle compatibility.
- [ ] run `ktlintCheck` and `detekt`.
- [ ] run `testDebugUnitTest`.
- [ ] run `assembleDebug` and `:app:assembleRelease`.
- [ ] run template smoke checks when build plugins or generators change.

## Repository references

- `gradle/libs.versions.toml`
- `settings.gradle.kts`
- `build-logic/convention`
- `renovate.json`
