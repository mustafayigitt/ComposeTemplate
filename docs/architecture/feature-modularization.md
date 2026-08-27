# Feature Modularization

Feature modularization is one of ComposeTemplate's most important architectural decisions.

Each feature is split into independent Gradle modules so ownership, dependencies, and build responsibilities are explicit.

## Problem

Package-only architecture can look organized but still allow accidental coupling. As features grow, a single module can accumulate screens, API models, repository implementations, navigation routes, and state handling in one place.

That creates common problems:

- UI imports network DTOs.
- ViewModels know concrete repository implementations.
- app-level navigation becomes a large central registry.
- feature tests require too much of the application graph.
- new features are created inconsistently.

## Standard feature structure

ComposeTemplate uses this structure:

```text
feature/{name}/
├── data/
├── domain/
├── navigation/
└── presentation/
```

| Module | Responsibility |
|---|---|
| `data` | repository implementation, DTOs, API/database access |
| `domain` | repository contracts, use cases, domain models |
| `navigation` | route objects, tab metadata, navigation contracts |
| `presentation` | ViewModels, UI state, events, Compose screens |

## Why four modules?

### `domain`

The domain module is the feature's stable center. It contains the contracts and models that other layers can depend on.

### `data`

The data module knows infrastructure details: Retrofit services, local storage, DTOs, and mapping logic. It implements domain contracts.

### `navigation`

The navigation module lets a feature own its route definitions without forcing the app module to define every route directly.

### `presentation`

The presentation module renders the feature. It owns ViewModels, state, events, and Composables.

## Dependency model

```text
feature:{name}:data
    -> feature:{name}:domain

feature:{name}:presentation
    -> feature:{name}:domain
    -> feature:{name}:navigation

feature:{name}:navigation
    -> core:navigation
```

The app module wires feature modules together, but the feature owns its internal shape.

## Minimal, medium, and full examples

ComposeTemplate intentionally includes features at different levels of complexity.

| Level | Examples | Use when |
|---|---|---|
| Minimal | `home`, `detail`, `list`, `search` | the feature mainly demonstrates routing and UI state |
| Medium | `profile`, `onboarding` | the feature uses preferences, multiple use cases, or more involved UI |
| Full | `auth`, `splash` | the feature demonstrates repository contracts, data flow, and cross-layer behavior |

This gives developers practical reference points when creating new features.

## Generator support

The `scaffoldFeature` task creates the four-module structure automatically:

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

With database starter files:

```bash
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

The task updates `settings.gradle.kts`, adds app dependencies, creates starter source files, and generates route/screen registration boilerplate.

## Trade-offs

Feature modularization introduces more modules and more Gradle wiring. For a very small app, this can be unnecessary.

For a production template, the benefits are stronger:

- consistent feature shape,
- clearer ownership,
- compiler-visible boundaries,
- easier generated-feature validation,
- smaller test targets,
- reduced risk of architecture drift.

## Checklist

- [ ] each feature has `data`, `domain`, `navigation`, and `presentation` modules.
- [ ] data depends on domain.
- [ ] presentation depends on domain and navigation.
- [ ] domain does not depend on data or presentation.
- [ ] route definitions are feature-owned.
- [ ] generated features compile after scaffolding.
- [ ] feature complexity matches the real need.

## Repository references

- `feature/auth`
- `feature/profile`
- `feature/home`
- `feature/detail`
- `build-logic/convention/ScaffoldFeaturePlugin.kt`
- `settings.gradle.kts`
