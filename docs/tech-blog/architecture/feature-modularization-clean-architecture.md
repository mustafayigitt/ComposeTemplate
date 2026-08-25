# Feature Modularization with Clean Architecture in Android

## Who this article is for

This article is for Android developers building apps that are becoming too large for a single module or package-based architecture.

## What you will learn

- Why folders are not enough for architecture
- How feature modularization supports Clean Architecture
- Why ComposeTemplate splits each feature into four modules
- How dependency direction should work
- Trade-offs and common mistakes

## The problem with package-only architecture

Many Android projects start with packages like this:

```text
feature/login
feature/home
data
network
ui
```

This is not wrong, but packages alone do not enforce boundaries. Any package can still import almost anything if it is in the same Gradle module.

Over time, common problems appear:

- UI imports DTOs directly.
- Repository implementations leak into screens.
- Domain models depend on Android framework classes.
- Navigation becomes centralized and hard to evolve.
- Tests need too much app context.

The project may look organized but still be tightly coupled.

## Clean Architecture mental model

Clean Architecture separates business rules from delivery mechanisms.

In Android terms:

- domain should describe what the app does,
- data should describe how information is fetched or stored,
- presentation should describe how users interact with state,
- navigation should describe how users move between screens.

The important rule is dependency direction. Inner layers should not depend on outer implementation details.

## ComposeTemplate feature structure

ComposeTemplate uses this structure for each feature:

```text
feature/{name}/
├── data/
├── domain/
├── navigation/
└── presentation/
```

| Module | Responsibility |
|---|---|
| `data` | Repository implementation, DTOs, Retrofit services, local data sources |
| `domain` | Use cases, domain models, repository contracts |
| `navigation` | Routes, navigation item contracts, tab metadata |
| `presentation` | ViewModels, UiState/Event, Compose screens |

## Dependency direction

The intended direction is:

```text
data → domain ← presentation
presentation → navigation
navigation → core:navigation
```

Domain does not know how data is fetched. Presentation does not need DTOs. Data implements domain contracts.

## Why navigation is its own module

Navigation often becomes a hidden coupling point. If every screen is registered in one central graph, adding a feature requires app-level edits.

ComposeTemplate lets features own their routes and screen providers. The app composes registered features instead of manually knowing every route.

This keeps feature ownership clearer.

## Minimal, medium and full features

Not every feature needs the same complexity.

A simple static screen may only need a small presentation and navigation setup. A full auth feature may need networking, repositories, use cases, token refresh logic, and tests.

ComposeTemplate includes different feature examples so developers can copy the right complexity level.

## Trade-offs

Feature modularization has costs:

- more Gradle modules,
- more build logic,
- more initial concepts,
- more explicit wiring.

The benefits appear as the project grows:

- clearer ownership,
- safer refactoring,
- better testability,
- faster mental navigation,
- stronger dependency boundaries.

## Common mistakes

### Creating modules without enforcing dependency direction

Modules alone do not guarantee good architecture. Dependency rules still matter.

### Making domain too thin

If domain only forwards calls, it may not be adding value. Use cases should represent meaningful behavior or policy.

### Letting DTOs leak upward

DTOs belong to data. UI should use domain or UI models.

### Putting all navigation in app

That centralizes feature knowledge and weakens modularity.

## Production checklist

- [ ] Each feature has clear ownership.
- [ ] Domain models are free from Android framework dependencies.
- [ ] Repository contracts live in domain.
- [ ] Repository implementations live in data.
- [ ] Presentation depends on use cases, not data sources.
- [ ] Navigation is feature-owned.
- [ ] The app module avoids feature internals.

## Summary

Feature modularization is not about having many folders. It is about making architecture visible and enforceable.

ComposeTemplate uses Gradle module boundaries to make Clean Architecture harder to accidentally break.
