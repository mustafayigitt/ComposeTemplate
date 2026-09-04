# Build Logic Convention Plugins

This directory contains custom Gradle Convention Plugins that standardize build configuration across the project.

## 📁 Structure

```
build-logic/
├── convention/
│   └── src/main/kotlin/com/ytapps/composetemplate/convention/
│       ├── AndroidApplicationConventionPlugin.kt
│       ├── AndroidComposeConventionPlugin.kt
│       ├── AndroidHiltConventionPlugin.kt
│       ├── AndroidLibraryConventionPlugin.kt       # applies static.analysis + module.boundary
│       ├── AndroidLibraryNativeConventionPlugin.kt # NDK secret management
│       ├── AndroidRoomConventionPlugin.kt
│       ├── AppModuleBoundaryPlugin.kt              # registers checkAppModuleBoundary
│       ├── BaselineProfileGeneratorConventionPlugin.kt
│       ├── CheckModuleBoundaryTask.kt              # the shared scanner, driven by task inputs
│       ├── CreateNewAppPlugin.kt
│       ├── FeatureDomainConventionPlugin.kt
│       ├── FeatureDataConventionPlugin.kt
│       ├── FeatureNavigationConventionPlugin.kt
│       ├── FeaturePresentationConventionPlugin.kt
│       ├── ModuleBoundaryPlugin.kt                 # registers checkModuleBoundary per module
│       ├── PerfConventionPlugin.kt                 # conditional baseline profile wiring
│       ├── ScaffoldFeaturePlugin.kt
│       ├── StaticAnalysisConventionPlugin.kt
│       ├── TestConventionPlugin.kt
│       ├── ValidateSecretsPlugin.kt
│       └── ProjectExtensions.kt
└── settings.gradle.kts
```

## ✨ Recent Improvements

- **Module discovery**: `settings.gradle.kts` finds modules on disk, so adding or removing one needs no build-file edit.
- **Enforced module boundaries**: the build fails when any module imports another module it is not allowed to name — not just `:app`.
- **Conditional performance tooling**: baseline profile wiring is applied only when the generator module exists.
- **Compose Metrics & Reports**: Integrated support for generating performance and stability metrics.
- **Secret Management**: Automated validation, native obfuscation, and artifact scanning support.
- **Centralized Versioning**: Categorized dependencies in Version Catalog for better maintainability.

## 🔌 Available Plugins

### `composetemplate.android.application`
**What it does:**
- Applies the Android application plugin, Kotlin Android plugin, and shared SDK/default config.
- Configures build types, packaging, and project-wide Android defaults.
- Also applies `composetemplate.static.analysis` and `composetemplate.app.boundary`.

### `composetemplate.android.application.compose`
**What it does:**
- Applies Kotlin Compose Compiler plugin.
- Enables Compose build features.
- Adds common Compose dependencies.
- **New**: Supports metrics and stability reports via `gradle.properties`.

### `composetemplate.android.library`
**What it does:**
- Applies the Android library plugin, Kotlin Android plugin, and shared library defaults.
- Keeps module SDK and packaging configuration consistent.
- Also applies `composetemplate.static.analysis` and `composetemplate.module.boundary`, which is how every library module gets a boundary check without opting in.

### `composetemplate.android.library.compose`
**What it does:**
- Applies the shared Compose setup for Android library modules.
- Reuses the same compiler metrics and reports toggles.

### `composetemplate.android.hilt`
**What it does:**
- Applies Hilt and KSP configuration.
- Adds Hilt dependencies used by app, feature, and core modules.

### `composetemplate.test`
**What it does:**
- Adds the common unit/UI test dependency set used across modules.
- Includes JUnit, Truth, MockK, coroutine testing, and AndroidX test libraries.

### `composetemplate.android.room`
**What it does:**
- Applies Room dependencies and KSP compiler setup.
- Configures schema export for database modules.

### `composetemplate.android.library.native`
**What it does:**
- Configures CMake and NDK.
- Injects obfuscated secrets from `secrets.properties` or environment variables as native/compiler definitions.

### `composetemplate.validate.secrets`
**What it does:**
- Fails builds when required secret values are missing, placeholders, weak, or malformed.
- Validates Retrofit base URL shape, signature hash format, certificate pinning config, and minimum version.

### `composetemplate.static.analysis`
**What it does:**
- Applies Ktlint and Detekt consistently across modules.
- Uses the shared Detekt config from `config/detekt/detekt.yml`.

### `composetemplate.app.boundary`
**What it does:**
- Registers the `checkAppModuleBoundary` verification task and hooks it into the application module's build.
- Fails the build when `:app` imports a symbol from any module other than `core:common`, `core:navigation` and `core:ui`.
- Writes a report to `build/reports/plugout/app-module-boundary.txt`.
- This is what keeps optional modules deletable: they must reach the app through DI multibindings rather than imports.

### `composetemplate.module.boundary`
**What it does:**
- Registers `checkModuleBoundary` for every Android library module, applied through `composetemplate.android.library` so no build script opts in.
- Derives the rule from the module's own Gradle path:
  - `core:common`, `core:navigation`, `core:ui` and `core:data` survive every plug-out combination, so they may name only each other. An import of an optional module from here would make that module undeletable everywhere.
  - Any other `core:*` module may name any core module but never a feature.
  - A `feature:X:*` module may name its own feature and any feature's `navigation` module — a feature's route contract is what it publishes — but not another feature's `domain`, `data` or `presentation` code.
- Writes a report to `build/reports/plugout/module-boundary.txt`, and hooks into `preBuild` and `check`.
- Narrow exceptions are declared per module, not in a shared file:

```kotlin
moduleBoundary {
    additionalPermittedImports.add("feature.auth.domain.")
}
```

- Both boundary plugins share one `CheckModuleBoundaryTask`. The task knows nothing about which module it is checking; guarded prefixes, permitted patterns and advice text all arrive as task inputs.

### `composetemplate.perf`
**What it does:**
- Applies `androidx.baselineprofile`, adds the `:baselineprofile` generator dependency and the `profileinstaller` runtime dependency — but **only if the `:baselineprofile` project is part of the build**.
- When the folder has been deleted, it logs that baseline profiles are disabled and does nothing else, so performance tooling can be plugged out with a folder delete.
- CI asserts that log line, so a change that silently made the wiring unconditional again would fail instead of passing.

### `composetemplate.scaffold.feature`
**What it does:**
- Generates `data`, `domain`, `navigation`, and `presentation` feature sub-modules.
- Creates a route, ViewModel, UI state/event, stateless screen, screen provider, Hilt binding, and localized string resources.
- Performs **no** build-file edits: module discovery registers the new folders, so the task logs `no edit needed` for `settings.gradle.kts` and `app/build.gradle.kts`.

### `composetemplate.create.new.app`
**What it does:**
- Copies the template into a sibling project with a new package and app name.
- Excludes local-only files such as `local.properties`, `secrets.properties`, `.git`, and build outputs.

### `composetemplate.baseline.profile.generator`
**What it does:**
- Applies the Baseline Profile generator setup used by the `:baselineprofile` module.

### Feature layer plugins
**What they do:**
- `composetemplate.feature.domain`: keeps domain modules lean with `:core:common`, Hilt, and tests.
- `composetemplate.feature.data`: adds data/network/database/secrets infrastructure for repository implementations.
- `composetemplate.feature.navigation`: adds typed route/navigation dependencies.
- `composetemplate.feature.presentation`: adds Compose, UI, navigation, Hilt, and test dependencies.

---

## 📦 Version Catalog Integration

All dependencies and versions are managed in `gradle/libs.versions.toml`.

### Key SDK Versions
- **minSdk**: 26
- **compileSdk**: 37
- **targetSdk**: 36
- **Kotlin**: 2.0.21

The `minSdk` baseline is Android 8.0 (Oreo). It is read from the catalog by every
convention plugin, so changing it there changes it for all 47 modules at once.

## 🔧 Configuring Metrics

You can enable Compose Metrics by toggling these flags in `gradle.properties`:

```properties
composetemplate.composeCompilerMetricsEnabled=true
composetemplate.composeCompilerReportsEnabled=true
```
