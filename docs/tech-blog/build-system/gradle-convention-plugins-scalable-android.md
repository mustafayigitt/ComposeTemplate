# Gradle Convention Plugins for Scalable Android Projects

## Who this article is for

This article is for Android developers maintaining multi-module projects where Gradle files are becoming repetitive, inconsistent, or hard to upgrade.

## What you will learn

- What convention plugins are
- Why they are useful in multi-module Android projects
- How they differ from custom tasks
- How ComposeTemplate uses them to encode architecture
- What mistakes to avoid

## The problem: build configuration drift

Every Android module needs configuration: plugin IDs, SDK versions, Kotlin options, Compose setup, Hilt setup, KSP processors, Room schemas, test dependencies, lint configuration, and static analysis.

If each module configures those manually, drift appears:

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 37
}
```

One module forgets test dependencies. Another uses different Compose options. Another misses Detekt. The build still works, but consistency is gone.

## What is a convention plugin?

A convention plugin is a Gradle plugin that captures project-specific defaults.

Instead of repeating configuration, a module declares its role:

```kotlin
plugins {
    id("composetemplate.feature.presentation")
}
```

That plugin can apply Android/Kotlin plugins, configure Compose, add dependencies, wire Hilt, and apply test conventions.

## Convention plugin vs custom task

A convention plugin configures a module. A task performs an action.

Examples:

- `composetemplate.android.library` configures library modules
- `composetemplate.feature.presentation` configures presentation modules
- `create-new-app` generates a new project
- `scaffoldFeature` generates feature modules

Mixing these concepts makes build logic harder to reason about.

## ComposeTemplate implementation

ComposeTemplate keeps build logic under:

```text
build-logic/convention/
```

The main build includes this build logic via `settings.gradle.kts`, making plugins available to app and library modules.

Important plugins include:

- Android application/library conventions
- Compose conventions
- Hilt conventions
- Room conventions
- feature layer conventions
- test conventions
- static analysis conventions
- native library conventions
- baseline profile generator conventions

## Build logic as architecture

The plugin a module applies communicates its architectural role. A domain module should not accidentally receive UI dependencies. A data module should not need Compose. A presentation module should get lifecycle and Compose dependencies consistently.

This turns build logic into a guardrail.

## Common mistakes

### One giant plugin

A single plugin that does everything becomes hard to understand. Prefer role-specific plugins.

### Hidden surprising dependencies

Convention plugins should reduce boilerplate, not make dependencies mysterious.

### Ignoring Gradle lifecycle

Avoid unnecessary work during configuration. Build logic should be predictable and compatible with modern Gradle practices.

### Bypassing the version catalog

Convention plugins should still use centralized dependency aliases.

## Production checklist

- [ ] Repeated build setup is moved into convention plugins.
- [ ] Module build files remain declarative.
- [ ] Plugin names reflect module roles.
- [ ] Feature layer plugins encode architecture boundaries.
- [ ] Dependencies come from the version catalog.
- [ ] CI validates generated modules and normal modules.

## Summary

Convention plugins are not just Gradle cleanup. In a large Android project, they are an architectural tool. ComposeTemplate uses them to keep module setup consistent, scalable, and generator-friendly.
