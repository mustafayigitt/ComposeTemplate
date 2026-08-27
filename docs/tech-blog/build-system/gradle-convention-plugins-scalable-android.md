# Gradle Convention Plugins for Scalable Android Projects

## Who this article is for

This article is for Android developers maintaining multi-module projects where Gradle files are becoming repetitive, inconsistent, or hard to upgrade.

## What you will learn

- what convention plugins are
- why they matter in multi-module Android projects
- how they differ from generator tasks
- how ComposeTemplate uses them to encode architecture
- what mistakes to avoid when centralizing build logic

## The problem

Every Android module needs build configuration: plugin IDs, SDK versions, Kotlin options, Compose setup, Hilt, KSP, Room, tests, static analysis, packaging, and dependency declarations.

When every module repeats this manually, build configuration drifts.

One module forgets Detekt. Another uses a different test stack. Another adds Compose to a domain module. Another misses KSP after a Hilt upgrade.

The build may still pass, but architectural consistency is gone.

## Why this matters for Android projects

In a large Android project, Gradle is not only a build tool. It is part of the architecture.

The build decides:

- which modules can depend on which APIs
- which compiler plugins run
- which code is generated
- which tests are available
- which release checks are enforced

If build logic is inconsistent, architecture becomes a social agreement instead of a constraint.

## Common approaches

### Repeat configuration in every module

Simple at first, but hard to upgrade and easy to drift.

### Shared Gradle script files

Better than repetition, but less type-safe and harder to structure as reusable build code.

### Convention plugins

Convention plugins let modules declare their role:

```kotlin
plugins {
    id("composetemplate.feature.presentation")
}
```

The plugin configures the expected defaults for that role.

## ComposeTemplate's approach

ComposeTemplate keeps build logic under:

```text
build-logic/convention
```

The root build includes it through `settings.gradle.kts`:

```kotlin
pluginManagement {
    includeBuild("build-logic")
}
```

This makes project-specific plugins available to app, core, and feature modules.

## Plugin taxonomy

ComposeTemplate separates plugins by responsibility:

- Android application/library plugins
- Compose plugins
- Hilt plugin
- Room plugin
- test plugin
- static analysis plugin
- native library plugin
- feature layer plugins
- generator plugins
- validation plugins
- baseline profile plugin

This avoids one giant plugin that does too much.

## Feature layer plugins

The feature plugins encode architecture:

| Plugin | Module role |
|---|---|
| `composetemplate.feature.domain` | lean domain module |
| `composetemplate.feature.data` | repository/data infrastructure module |
| `composetemplate.feature.navigation` | route and navigation metadata module |
| `composetemplate.feature.presentation` | ViewModel and Compose UI module |

Generated features use the same plugins as hand-written features, so scaffolding stays aligned with architecture.

## Convention plugin vs task

A convention plugin configures a module. A task performs work.

Examples:

- `composetemplate.feature.presentation` configures a presentation module
- `scaffoldFeature` generates modules
- `create-new-app` generates a project
- `validateSecrets` validates configuration

Keeping this distinction clear makes the build easier to reason about.

## Design trade-offs

Convention plugins require Gradle knowledge and maintenance. They can also hide dependencies if poorly named or poorly documented.

ComposeTemplate mitigates this with role-specific plugin names and documentation that maps plugins to module responsibilities.

## Production checklist

- [ ] repeated module setup is centralized
- [ ] plugin names reflect architectural roles
- [ ] dependencies come from the version catalog
- [ ] generated modules use the same plugins as existing modules
- [ ] domain modules stay free of UI dependencies
- [ ] CI validates generated modules

## Takeaways

- Convention plugins are architecture tools, not only Gradle cleanup.
- Role-specific plugins reduce configuration drift.
- Generated code should use the same build conventions as hand-written code.
- Build logic must stay documented because hidden build behavior becomes technical debt.
