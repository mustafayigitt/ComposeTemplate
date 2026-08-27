# Architecture Overview

ComposeTemplate separates application composition, shared infrastructure, product features, build logic, and performance tooling into explicit Gradle modules.

The architecture is designed for projects that are expected to grow. It prioritizes clear boundaries, repeatable feature creation, testability, and build-level enforcement over minimal initial file count.

## Problem

Many Android projects start with a clean package layout but keep most code inside one Gradle module. That works early, but as the project grows it becomes easy for UI code to import DTOs, repository implementations to leak into ViewModels, navigation to centralize in the app module, and business rules to become mixed with framework code.

ComposeTemplate avoids that by making architecture visible in the module graph.

## High-level structure

```text
ComposeTemplate/
├── app/
├── core/
├── feature/
├── build-logic/
├── benchmark/
├── baselineprofile/
└── docs/
```

| Area | Responsibility |
|---|---|
| `app` | Composition root, application identity, app-level wiring |
| `core/*` | Shared infrastructure used by features and app |
| `feature/*` | Product features split by layer |
| `build-logic` | Convention plugins and generator tasks |
| `benchmark` | Macrobenchmark tests |
| `baselineprofile` | Baseline Profile generation |
| `docs` | MkDocs documentation source |

## App module

The `app` module is the composition root. It owns application-level configuration such as namespace, application id, build types, signing configuration, baseline profile dependency, and high-level module wiring.

It should not become a business-logic module.

The app module may depend on feature modules so it can assemble the full application graph, but features should own their routes, screens, state, and data behavior.

## Core modules

`core/*` modules provide shared infrastructure:

| Module | Responsibility |
|---|---|
| `core:common` | shared result type, constants, dispatchers, contracts |
| `core:data` | DataStore preferences and locale handling |
| `core:database` | Room database foundation |
| `core:network` | Retrofit, OkHttp, auth infrastructure, `BaseRepository` |
| `core:navigation` | Navigation contracts, back stack manager, screen registry |
| `core:ui` | theme, shared UI, `BaseViewModel` |
| `core:secrets` | secret access and native-backed secret loading |
| `core:security` | runtime integrity signals and reports |
| `core:analytics` | analytics abstraction and implementation |
| `core:config` | app configuration and update contracts |
| `core:permission` | runtime permission helpers |
| `core:google-play` | in-app review and update integrations |

Core modules should stay reusable. They must not depend on product feature modules.

## Feature modules

Every feature follows the same layered shape:

```text
feature/{name}/
├── data
├── domain
├── navigation
└── presentation
```

This gives each feature a predictable place for repository contracts, repository implementations, routes, ViewModels, UI state, events, and screens.

## Dependency direction

The intended dependency direction is:

```text
data -> domain <- presentation
presentation -> navigation
navigation -> core:navigation
```

Domain is the center of a feature. Data implements domain contracts. Presentation consumes domain use cases and renders state. Navigation exposes route-level contracts and participates in app navigation without centralizing every screen in `app`.

## Feature complexity spectrum

The template includes features at different complexity levels so developers can copy the right pattern:

| Feature | Complexity | Demonstrates |
|---|---|---|
| `auth` | Full | API service, repository, token refresh contract, domain model, ViewModel |
| `splash` | Full | repository-driven start destination logic |
| `profile` | Medium | preferences-backed theme/language behavior |
| `onboarding` | Medium | pager-style flow and completion state |
| `home` | Minimal | bottom-bar tab structure |
| `list` | Minimal | list flow with navigation |
| `search` | Minimal | filterable UI state |
| `detail` | Minimal | parameterized route handling |

## Build logic as architecture

ComposeTemplate uses convention plugins to encode module roles. A presentation module applies a presentation plugin; a data module applies a data plugin; a domain module remains lean.

This reduces drift and makes generated modules consistent with hand-written modules.

## Trade-offs

This architecture introduces more modules and more explicit wiring than a simple single-module app. It also requires developers to understand Gradle module boundaries and convention plugins.

The trade-off is intentional. ComposeTemplate optimizes for long-term maintainability, generator consistency, and production readiness.

## Checklist

- [ ] `app` stays a composition root, not a business-logic module.
- [ ] `core` modules do not depend on feature modules.
- [ ] each feature keeps `data`, `domain`, `navigation`, and `presentation` responsibilities separate.
- [ ] dependency direction flows toward domain.
- [ ] generated features follow the same structure as existing features.
- [ ] build logic encodes module roles consistently.

## Related documentation

- [Feature Modularization](feature-modularization.md)
- [Clean Architecture](clean-architecture.md)
- [Navigation Architecture](navigation-architecture.md)
- [UI State Management](ui-state-management.md)
- [Module Map](../reference/module-map.md)
