# Gradle Convention Plugins

Gradle convention plugins are the backbone of ComposeTemplate's build system.

They keep module build files declarative, reduce configuration drift, and encode architectural roles directly into Gradle.

## Problem

Multi-module Android projects repeat the same setup across many modules:

- Android application/library plugins,
- Kotlin configuration,
- Compose compiler setup,
- Hilt and KSP wiring,
- Room dependencies and schema export,
- test dependencies,
- static analysis,
- SDK defaults,
- packaging options.

When every module configures these manually, drift is almost guaranteed. One module misses test dependencies, another uses different Compose settings, another forgets Detekt, and another adds dependencies that do not match its architectural role.

## Design goals

ComposeTemplate uses convention plugins to:

- centralize repeated Gradle configuration,
- keep module `build.gradle.kts` files small,
- make module roles obvious,
- reduce dependency drift,
- support generated modules,
- keep architecture consistent across hand-written and scaffolded features.

## Included build setup

The root `settings.gradle.kts` includes build logic with:

```kotlin
pluginManagement {
    includeBuild("build-logic")
}
```

This makes custom plugins from `build-logic/convention` available to the main build.

## Convention plugin vs task

A convention plugin configures a module.

A task performs an action.

Examples:

| Type | Examples |
|---|---|
| Convention plugin | `composetemplate.feature.presentation`, `composetemplate.android.hilt` |
| Task | `create-new-app`, `scaffoldFeature`, `validateSecrets` |

Keeping this distinction clear makes build logic easier to maintain.

## Plugin families

ComposeTemplate's convention plugins fall into several families.

### Android module plugins

| Plugin | Purpose |
|---|---|
| `composetemplate.android.application` | app module defaults |
| `composetemplate.android.library` | Android library defaults |
| `composetemplate.android.library.native` | NDK/CMake setup for native-backed secrets |

### Compose plugins

| Plugin | Purpose |
|---|---|
| `composetemplate.android.application.compose` | Compose setup for app modules |
| `composetemplate.android.library.compose` | Compose setup for library modules |

These plugins also support Compose compiler metrics and reports through Gradle properties.

### Infrastructure plugins

| Plugin | Purpose |
|---|---|
| `composetemplate.android.hilt` | Hilt and KSP wiring |
| `composetemplate.android.room` | Room dependencies and schema setup |
| `composetemplate.test` | shared JUnit, Truth, MockK, coroutine, and AndroidX test dependencies |
| `composetemplate.static.analysis` | Ktlint and Detekt setup |

### Feature layer plugins

| Plugin | Intended module |
|---|---|
| `composetemplate.feature.domain` | `feature:{name}:domain` |
| `composetemplate.feature.data` | `feature:{name}:data` |
| `composetemplate.feature.navigation` | `feature:{name}:navigation` |
| `composetemplate.feature.presentation` | `feature:{name}:presentation` |

These plugins make feature modules consistent and help encode architecture into the build.

### Generator and validation plugins

| Plugin | Exposes |
|---|---|
| `composetemplate.create.new.app` | `create-new-app` task |
| `composetemplate.scaffold.feature` | `scaffoldFeature` task |
| `composetemplate.validate.secrets` | `validateSecrets`, `scanApkForSecrets`, `hardeningReport` |
| `composetemplate.baseline.profile.generator` | baseline profile generator setup |

## Module build files stay declarative

A domain module can stay small:

```kotlin
plugins {
    id("composetemplate.feature.domain")
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.domain"
}
```

A presentation module declares its role and explicit feature dependencies:

```kotlin
plugins {
    id("composetemplate.feature.presentation")
}

dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:navigation"))
}
```

The convention plugin handles shared Compose, Hilt, lifecycle, and test dependencies.

## Build logic as architecture

A module's plugin communicates what that module is allowed to be.

A domain module should not need Compose dependencies. A data module should not need Composables. A presentation module should not manually repeat every UI dependency.

Convention plugins cannot replace architectural review, but they reduce accidental inconsistency.

## Generated module consistency

`scaffoldFeature` creates feature modules with the same convention plugins used by hand-written features. This means generated features enter the project with the expected dependency shape.

That is important for a template repository: generated output must follow the same architecture as the template itself.

## Common mistakes

### Creating one giant convention plugin

A single plugin that configures everything becomes difficult to reason about. ComposeTemplate favors role-specific plugins.

### Hiding surprising dependencies

Convention plugins should reduce boilerplate, not make the build mysterious. If a plugin adds dependencies, its name and documentation should make that role clear.

### Bypassing the version catalog

Dependencies should remain centrally governed through `gradle/libs.versions.toml`.

### Doing expensive work during configuration

Build logic should avoid unnecessary configuration-time work and stay compatible with modern Gradle behavior.

## Checklist

- [ ] repeated Gradle setup is centralized.
- [ ] module build files declare roles clearly.
- [ ] feature modules use feature layer plugins.
- [ ] dependencies are sourced from the version catalog.
- [ ] generated modules use the same conventions as existing modules.
- [ ] build logic does not hide unexpected cross-layer dependencies.

## Repository references

- `build-logic/convention`
- `build-logic/README.md`
- `settings.gradle.kts`
- `gradle/libs.versions.toml`
- `build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention`
