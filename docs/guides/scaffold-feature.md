# Scaffold a Feature

This guide explains how to generate a new feature using ComposeTemplate's `scaffoldFeature` task.

## What this guide covers

You will create a feature with the standard four-module structure and optionally generate Room starter files.

## Command

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

To include Room starter files:

```bash
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

## Feature name rules

`featureName` must be lower snake case:

```text
settings
user_profile
payment_history
```

Avoid spaces, uppercase letters, and hyphens.

## Generated structure

The task creates:

```text
feature/settings/
├── data/
├── domain/
├── navigation/
└── presentation/
```

## Generated files

The scaffold includes:

- Gradle build files for each submodule,
- a type-safe navigation route,
- a starter domain use case,
- `UiState`,
- `Event`,
- `ViewModel`,
- route composable,
- screen composable,
- `ScreenProvider`,
- Hilt multibinding module,
- English and Turkish string resources.

When `-PwithDatabase=true` is used, it also creates:

- a Room `Entity`,
- a Room `Dao`.

## Automatic wiring

The task updates:

- `settings.gradle.kts`,
- `app/build.gradle.kts`.

This means the generated feature is immediately part of the project graph.

## Validate the generated feature

Run:

```bash
./gradlew :feature:settings:presentation:compileDebugKotlin
```

If generated with database support, also run:

```bash
./gradlew :feature:settings:data:compileDebugKotlin
```

## Adapt the generated code

The scaffold is intentionally minimal. After generation:

1. replace placeholder screen content,
2. add real domain contracts,
3. add repository implementation if needed,
4. register bottom-bar metadata if the feature is a tab,
5. add tests for ViewModel, use cases, and data layer behavior.

## Repository references

- `build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/ScaffoldFeaturePlugin.kt`
- `feature/auth`
- `feature/profile`
- `core/navigation`
