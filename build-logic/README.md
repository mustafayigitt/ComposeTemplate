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


## 📦 Version Catalog Integration

Convention Plugins work seamlessly with the Version Catalog (`gradle/libs.versions.toml`):

### SDK Versions
```toml
[versions]
minSdk = "23"
compileSdk = "36"
targetSdk = "36"
```

These are automatically applied by the Convention Plugins, so you don't need to specify them in individual module build files.

### Dependencies
```toml
[libraries]
androidx-core = { module = "androidx.core:core-ktx", version.ref = "androidx-core" }
```

Access in build files:
```kotlin
dependencies {
    implementation(libs.androidx.core)
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
