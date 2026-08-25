# Feature Modularization with Clean Architecture in Android

## Who this article is for

This article is for Android developers who are moving beyond package-based organization and want to understand how Gradle module boundaries can enforce architecture.

## What you will learn

- Why packages are not enough to protect architecture
- How Clean Architecture maps to Android feature modules
- Why ComposeTemplate uses `data`, `domain`, `navigation`, and `presentation` modules per feature
- How dependency direction protects maintainability
- How to avoid common modularization mistakes

## The problem with package-only architecture

A common Android structure starts like this:

```text
app/
  feature/login/
  feature/home/
  data/
  network/
  ui/
```

It looks organized, but every package still lives inside the same Gradle module. Any class can import almost any other class. Over time, boundaries become social agreements instead of compiler-enforced rules.

Common symptoms include:

- Compose screens importing API DTOs directly
- ViewModels constructing repository implementations
- domain models depending on Android framework types
- navigation routes spread across the app module
- tests needing large parts of the app graph

The codebase may look layered, but it is still tightly coupled.

## Clean Architecture mental model

Clean Architecture separates policy from details.

In Android terms:

- `domain` describes business behavior and contracts
- `data` knows how to fetch and store information
- `presentation` turns state into UI and user actions into intents
- `navigation` describes how the feature is reached and rendered

The most important rule is dependency direction. Inner policy should not depend on outer implementation details.

## ComposeTemplate feature structure

ComposeTemplate models each feature as four modules:

```text
feature/{name}/
├── data/
├── domain/
├── navigation/
└── presentation/
```

| Module | Responsibility | Should not contain |
|---|---|---|
| `domain` | use cases, domain models, repository contracts | Retrofit, Room, Compose, Android UI |
| `data` | repository implementation, DTOs, remote/local data sources | Composables, UI state |
| `navigation` | route objects, navigation contracts, tab metadata | screen implementation details |
| `presentation` | ViewModels, UiState/Event, Composables | Retrofit services, database DAOs |

## Dependency direction

A simplified dependency model is:

```text
data → domain ← presentation
presentation → navigation
navigation → core:navigation
```

The direction tells an important story: data implements domain contracts; presentation consumes domain use cases; navigation exposes feature entry points without forcing app-level knowledge of feature internals.

## Why navigation is separated

Navigation often becomes a hidden coupling point. If all route-to-screen mapping lives in `app`, every feature change requires app-level edits. That makes feature ownership weaker.

By keeping route definitions and screen registration feature-owned, ComposeTemplate lets a feature say:

> This is my route. This is how I render it. This is how I participate in app navigation.

The app module orchestrates, but it does not need to know every implementation detail.

## Trade-offs

Feature modularization is not free. It adds modules, Gradle configuration, and more explicit wiring. For a tiny app, it can be too much.

But for a template designed to scale, the benefits are significant:

- architecture is visible in the project structure
- dependencies are harder to misuse accidentally
- features have clearer ownership
- generated features can follow the same convention
- tests can target smaller boundaries

## Common mistakes

### Making modules but ignoring dependency direction

Modules alone do not guarantee architecture. If presentation can depend on data implementations, the boundary is weak.

### Creating anemic domain layers

A domain layer that only forwards calls may not be valuable. Use cases should represent real behavior, policy, or orchestration.

### Letting DTOs leak into UI

DTOs represent transport details. UI should depend on domain or UI models.

### Keeping all navigation in the app module

This recreates central coupling and makes feature ownership weaker.

## Production checklist

- [ ] Domain models are independent of Android UI and network frameworks.
- [ ] Repository contracts live in domain.
- [ ] Repository implementations live in data.
- [ ] Presentation depends on use cases, not Retrofit or Room directly.
- [ ] Feature navigation is owned by the feature.
- [ ] The app module does not become a route registry for every feature.
- [ ] Generated features follow the same structure as hand-written features.

## Summary

Feature modularization is not about having many folders. It is about making architecture enforceable. ComposeTemplate uses Gradle module boundaries so Clean Architecture is protected by the build, not only by team discipline.
