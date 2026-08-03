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
│       ├── AndroidLibraryConventionPlugin.kt
│       ├── AndroidLibraryNativeConventionPlugin.kt # NDK secret management
│       ├── AndroidRoomConventionPlugin.kt
│       ├── BaselineProfileGeneratorConventionPlugin.kt
│       ├── CreateNewAppPlugin.kt
│       ├── FeatureDomainConventionPlugin.kt
│       ├── FeatureDataConventionPlugin.kt
│       ├── FeatureNavigationConventionPlugin.kt
│       ├── FeaturePresentationConventionPlugin.kt
│       ├── ScaffoldFeaturePlugin.kt
│       ├── StaticAnalysisConventionPlugin.kt
│       ├── TestConventionPlugin.kt
│       ├── ValidateSecretsPlugin.kt
│       └── ProjectExtensions.kt
└── settings.gradle.kts
```

## ✨ Recent Improvements

- **Compose Metrics & Reports**: Integrated support for generating performance and stability metrics.
- **Secret Management**: Automated validation, native obfuscation, and artifact scanning support.
- **Centralized Versioning**: Categorized dependencies in Version Catalog for better maintainability.

## 🔌 Available Plugins

### `composetemplate.android.application`
**What it does:**
- Applies the Android application plugin, Kotlin Android plugin, and shared SDK/default config.
- Configures build types, packaging, and project-wide Android defaults.

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

### `composetemplate.scaffold.feature`
**What it does:**
- Generates `data`, `domain`, `navigation`, and `presentation` feature sub-modules.
- Wires settings/app dependencies.
- Creates a route, ViewModel, UI state/event, stateless screen, screen provider, Hilt binding, and localized string resources.

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
- **minSdk**: 23
- **compileSdk**: 37
- **targetSdk**: 36
- **Kotlin**: 2.3.21

## 🔧 Configuring Metrics

You can enable Compose Metrics by toggling these flags in `gradle.properties`:

```properties
composetemplate.composeCompilerMetricsEnabled=true
composetemplate.composeCompilerReportsEnabled=true
```
