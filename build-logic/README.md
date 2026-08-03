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
│       ├── FeatureDomainConventionPlugin.kt
│       ├── FeatureDataConventionPlugin.kt
│       ├── FeatureNavigationConventionPlugin.kt
│       ├── FeaturePresentationConventionPlugin.kt
│       ├── ScaffoldFeaturePlugin.kt
│       ├── StaticAnalysisConventionPlugin.kt
│       ├── TestConventionPlugin.kt
│       └── ProjectExtensions.kt
└── settings.gradle.kts
```

## ✨ Recent Improvements

- **Compose Metrics & Reports**: Integrated support for generating performance and stability metrics.
- **Secret Management**: Automated validation, native obfuscation, and artifact scanning support.
- **Centralized Versioning**: Categorized dependencies in Version Catalog for better maintainability.

## 🔌 Available Plugins

### `composetemplate.android.application.compose`
**What it does:**
- Applies Kotlin Compose Compiler plugin.
- Enables Compose build features.
- Adds common Compose dependencies.
- **New**: Supports metrics and stability reports via `gradle.properties`.

### `composetemplate.android.library.native`
**What it does:**
- Configures CMake and NDK.
- Injects obfuscated secrets from `secrets.properties` or environment variables as native/compiler definitions.

### `composetemplate.scaffold.feature`
**What it does:**
- Generates `data`, `domain`, `navigation`, and `presentation` feature sub-modules.
- Wires settings/app dependencies.
- Creates a route, ViewModel, UI state/event, stateless screen, screen provider, Hilt binding, and localized string resources.

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
