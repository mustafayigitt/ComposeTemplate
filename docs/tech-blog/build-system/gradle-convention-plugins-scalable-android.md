# Gradle Convention Plugins for Scalable Android Projects

## Who this article is for

This article is for Android developers working on multi-module projects where build files are becoming repetitive, inconsistent, or difficult to maintain.

## What you will learn

- What Gradle convention plugins are
- Why multi-module Android projects need them
- How they differ from copy-pasted Gradle snippets
- How ComposeTemplate uses build logic as architecture
- Common mistakes and production checklist

## The problem: build configuration drift

A multi-module Android project often starts simple. Then more modules appear:

- app module,
- core modules,
- feature modules,
- benchmark modules,
- test utilities,
- native modules.

Each module needs Gradle configuration. Without a shared model, every `build.gradle.kts` starts repeating similar setup:

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 37
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
```

Soon, some modules use one dependency version, some forget test dependencies, some configure Compose slightly differently, and some miss static analysis.

This is build configuration drift.

## What is a convention plugin?

A convention plugin is a custom Gradle plugin that captures shared build rules for a project.

Instead of repeating setup in every module, modules apply a project-specific plugin:

```kotlin
plugins {
    id("composetemplate.feature.presentation")
}
```

That plugin can apply Android/Kotlin plugins, configure SDK versions, add dependencies, configure Compose, wire Hilt, and apply test setup.

## Convention plugin vs custom task

A convention plugin defines how a module should be configured.

A custom task performs an action.

For example:

- `composetemplate.android.library` is convention plugin behavior.
- `create-new-app` is a task that generates a project.
- `scaffoldFeature` is a task that generates feature modules.

Keeping this distinction clear prevents build logic from becoming confusing.

## ComposeTemplate implementation

ComposeTemplate keeps build logic under:

```text
build-logic/convention/
```

The project uses an included build via `settings.gradle.kts`, so the convention plugins are available to the main build.

Important plugins include:

- `composetemplate.android.application`
- `composetemplate.android.library`
- `composetemplate.android.library.native`
- `composetemplate.android.hilt`
- `composetemplate.android.room`
- `composetemplate.feature.data`
- `composetemplate.feature.domain`
- `composetemplate.feature.navigation`
- `composetemplate.feature.presentation`
- `composetemplate.test`
- `composetemplate.static.analysis`

## Why feature layer plugins matter

Feature modules are architecture boundaries. Build logic should respect that.

A data module needs different dependencies than a domain module. A presentation module needs Compose and lifecycle support. A navigation module needs route and navigation contracts.

By using feature-specific convention plugins, ComposeTemplate makes the intended architecture easy to apply.

## Benefits

### Cleaner module build files

Module build files become declarations of intent instead of long configuration scripts.

### Consistency

Every module that applies the same plugin receives the same setup.

### Easier upgrades

SDK, Kotlin, Compose, Hilt, Room and testing changes can be centralized.

### Architectural enforcement

The plugin a module applies communicates its role.

## Common mistakes

### Putting too much logic into one plugin

One giant plugin becomes hard to understand. Prefer smaller plugins that match module roles.

### Hiding surprising dependencies

Convention plugins should reduce boilerplate, not hide unexpected behavior.

### Ignoring Gradle lifecycle

Plugins should be careful about configuration timing and avoid unnecessary work during configuration.

### Bypassing version catalog

Convention plugins should still use centralized dependency aliases.

## Production checklist

- [ ] Repeated build logic lives in convention plugins.
- [ ] Module build files remain declarative.
- [ ] Feature plugins match architecture layers.
- [ ] SDK versions come from a central source.
- [ ] Dependency versions come from version catalog.
- [ ] Static analysis and test setup are applied consistently.
- [ ] Build logic changes are covered by CI.

## Summary

Gradle convention plugins are not just build cleanup. In a serious multi-module Android project, they become part of the architecture.

ComposeTemplate uses convention plugins to keep module setup consistent, reduce copy-paste, encode feature boundaries, and make project generation scalable.
