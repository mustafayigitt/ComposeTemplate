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
│       ├── FeatureConventionPlugin.kt
│       ├── TestConventionPlugin.kt
│       ├── KotlinAndroid.kt
│       └── ProjectExtensions.kt
└── settings.gradle.kts
```

## 🎯 Why Convention Plugins?

Convention Plugins help you:
- **Reduce duplication**: Share common build logic across modules
- **Centralize configuration**: Manage SDK versions, dependencies in one place
- **Improve maintainability**: Update configuration once, apply everywhere
- **Type-safe**: Use Kotlin instead of Groovy for build scripts

## ✨ Recent Improvements

- **New Test Plugin**: Eliminates 60+ lines of repeated test dependencies across 15+ modules
- **New Feature Plugin**: Provides consistent base dependencies for all feature modules
- **Organized Version Catalog**: Better categorized dependencies and versions for improved maintainability
- **Cleaner Module Builds**: Simplified build.gradle.kts files with less duplication

## 🔌 Available Plugins

### `composetemplate.android.application`
**What it does:**
- Applies Android Application plugin
- Applies Kotlin Android plugin
- Configures SDK versions (from `libs.versions.toml`)
- Sets up Java/Kotlin compatibility (Java 17, JVM 17)
- Configures packaging options

**Usage:**
```kotlin
plugins {
    id("composetemplate.android.application")
}
```

### `composetemplate.android.library`
**What it does:**
- Applies Android Library plugin
- Applies Kotlin Android plugin
- Configures SDK versions (from `libs.versions.toml`)
- Sets up Java/Kotlin compatibility (Java 17, JVM 17)
- Configures test options and packaging

**Usage:**
```kotlin
plugins {
    id("composetemplate.android.library")
}
```

### `composetemplate.android.application.compose`
**What it does:**
- Applies Kotlin Compose Compiler plugin
- Enables Compose build features
- Adds common Compose dependencies (UI, Material3, Tooling)
- Adds Compose BOM for version alignment

**Usage:**
```kotlin
plugins {
    id("composetemplate.android.application.compose")
}
```

### `composetemplate.android.hilt`
**What it does:**
- Applies Hilt Android plugin
- Applies KSP (Kotlin Symbol Processing)
- Adds Hilt dependencies
- Adds Hilt Navigation Compose

**Usage:**
```kotlin
plugins {
    id("composetemplate.android.hilt")
}
```

### `composetemplate.test`
**What it does:**
- Adds common testing dependencies (JUnit, Truth, MockK, Coroutines testing)
- Adds Android testing dependencies (JUnit Android, Espresso)
- Eliminates test dependency duplication across modules

**Usage:**
```kotlin
plugins {
    id("composetemplate.test")
}
```

### `composetemplate.feature`
**What it does:**
- Adds common feature module dependencies (:core, :contract)
- Provides consistent base dependencies for all feature modules

**Usage:**
```kotlin
plugins {
    id("composetemplate.feature")
}
```

## 💡 Practical Usage Examples

### App Module
```kotlin
plugins {
    id("composetemplate.android.application")
    id("composetemplate.android.application.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
    alias(libs.plugins.kotlin.serialization)
}
```

### Feature Presentation Module
```kotlin
plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.feature")  // Provides :core and :contract
    id("composetemplate.test")     // Provides all test dependencies
}
```

### Feature Domain Module
```kotlin
plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}
```


## 📦 Version Catalog Integration

Convention Plugins work seamlessly with the Version Catalog (`gradle/libs.versions.toml`):

### Organized Version Structure
```toml
[versions]
# SDK versions
minSdk = "23"
compileSdk = "36"
targetSdk = "36"

# AndroidX
androidx-core = "1.13.1"
androidx-compose-bom = "2024.06.00"
androidx-lifecycle = "2.8.4"

# Network
retrofit = "2.11.0"
converter-gson = "2.11.0"

# Testing
junit = "4.13.2"
truth = "1.1.5"
mockk = "1.13.7"
```

### Categorized Dependencies
```toml
[libraries]
# AndroidX Core
androidx-core = { module = "androidx.core:core-ktx", version.ref = "androidx-core" }
androidx-lifecycle-runtime-ktx = { module = "androidx.lifecycle:lifecycle-runtime-ktx", version.ref = "androidx-lifecycle" }

# Compose
androidx-compose-bom = { module = "androidx.compose:compose-bom", version.ref = "androidx-compose-bom" }
androidx-ui = { module = "androidx.compose.ui:ui" }
androidx-material3 = { module = "androidx.compose.material3:material3" }

# Testing
junit = { module = "junit:junit", version.ref = "junit" }
truth = { module = "com.google.truth:truth", version.ref = "truth" }
mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
```

Access in build files:
```kotlin
dependencies {
    implementation(libs.androidx.core)
    testImplementation(libs.junit)
}
```

## 🏗️ How It Works

1. **Convention Plugins are defined** in `build-logic/convention/`
2. **Plugins are registered** in `build-logic/settings.gradle.kts`
3. **Plugins are applied** in module `build.gradle.kts` files
4. **Configuration is centralized** using Version Catalog

### Example Flow:

```
app/build.gradle.kts
    ↓ applies
composetemplate.android.application
    ↓ reads from
libs.versions.toml (compileSdk, minSdk, targetSdk)
    ↓ configures
Android SDK, Kotlin, Java versions
```

## 🔧 Modifying Convention Plugins

To change SDK versions or dependencies:

1. **Update Version Catalog** (`gradle/libs.versions.toml`)
   ```toml
   [versions]
   compileSdk = "37"  # Update here
   ```

2. **Convention Plugin automatically picks it up**
   ```kotlin
   compileSdk = libs.findVersion("compileSdk").get().toString().toInt()
   ```

3. **All modules using the plugin get the update**

## 📝 Best Practices

1. **Keep plugins focused**: Each plugin should have a single responsibility
2. **Use Version Catalog**: Never hardcode versions in Convention Plugins
3. **Document changes**: Update this README when adding new plugins
4. **Test thoroughly**: Changes affect all modules using the plugin
5. **Organize dependencies**: Group related dependencies in version catalog with clear categories
6. **Leverage convention plugins**: Use `test` and `feature` plugins to eliminate duplication

## 🚀 Adding a New Convention Plugin

1. Create new file in `convention/src/main/kotlin/.../convention/`
2. Implement `Plugin<Project>` interface
3. Register in `convention/build.gradle.kts`
4. Apply in module `build.gradle.kts`

Example:
```kotlin
class MyConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Your configuration here
        }
    }
}
```

## 📚 Resources

- [Gradle Convention Plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html)
- [Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html)
- [Android Build Configuration](https://developer.android.com/build)
