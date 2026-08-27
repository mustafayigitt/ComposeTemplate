# Scaffold Feature

`scaffoldFeature` turns feature creation into a repeatable generator workflow.

## Problem

Feature setup in a modular Android project is repetitive and error-prone.

A developer must usually create modules, configure Gradle, add source sets, write route classes, create ViewModel/state files, register screens, add Hilt bindings, update settings, and wire app dependencies.

If this is done manually every time, feature structure drifts.

## Command

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

With Room starter files:

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

## Generated modules

The task creates:

```text
feature/{name}/
├── data
├── domain
├── navigation
└── presentation
```

Each module receives the appropriate convention plugin.

## Generated source files

The scaffold creates starter files for:

- navigation route,
- domain use case,
- UI state,
- event type,
- ViewModel,
- route composable,
- screen composable,
- screen provider,
- Hilt multibinding module,
- localized string resources.

When database support is enabled, it also creates:

- Room entity,
- Room DAO.

## Automatic wiring

The task updates:

- `settings.gradle.kts`,
- `app/build.gradle.kts`.

This ensures the generated feature is included in the project graph immediately.

## Generated code is a starting point

The scaffold provides the minimum correct structure. It is not meant to produce final business logic.

After generation, replace placeholder behavior with real requirements:

- add domain models,
- add repository contracts,
- implement data sources,
- customize navigation behavior,
- add tests,
- register bottom-bar metadata if needed.

## CI validation

CI generates both a standard feature and a database-backed feature, then compiles the generated modules. This protects the generator from regressions.

## Checklist

- [ ] feature name is valid lower snake case.
- [ ] four modules are generated.
- [ ] settings include entries are added.
- [ ] app dependencies are added.
- [ ] presentation module compiles.
- [ ] data module compiles when database support is enabled.
- [ ] placeholder code is adapted to real feature needs.

## Repository references

- `build-logic/convention/ScaffoldFeaturePlugin.kt`
- `settings.gradle.kts`
- `app/build.gradle.kts`
- `.github/workflows/ci.yml`
