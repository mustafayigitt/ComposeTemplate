# Feature Modularization with Clean Architecture in Android

## Who this article is for

This article is for Android developers moving beyond package-based organization toward build-enforced architecture.

## What you will learn

- why package-only layering is fragile
- how Clean Architecture maps to feature modules
- why ComposeTemplate uses `data`, `domain`, `navigation`, and `presentation`
- how scaffolding keeps feature creation consistent
- what trade-offs modularization introduces

## The problem

A project can have folders named `data`, `domain`, and `presentation` while still allowing any class to import any other class. In a single Gradle module, boundaries are mostly discipline.

Over time, this leads to common problems:

- Compose screens import DTOs
- ViewModels depend on repository implementations
- business rules move into UI code
- navigation becomes centralized in `app`
- tests require too much infrastructure

## Why this matters for Android projects

Android projects grow by feature. If features do not have clear boundaries, every new screen increases coupling.

Gradle modules make boundaries visible to the build. They help prevent accidental imports and make feature ownership clearer.

## Common approaches

### Single module with packages

Simple, fast, and good for small apps, but hard to enforce as the project grows.

### Layer modules only

Modules such as `data`, `domain`, and `presentation` can help, but they can become large shared buckets.

### Feature plus layer modules

Each feature owns its internal layers. This is the ComposeTemplate approach.

## ComposeTemplate's approach

Every feature follows:

```text
feature/{name}/
├── data
├── domain
├── navigation
└── presentation
```

The dependency direction is:

```text
data -> domain <- presentation
presentation -> navigation
navigation -> core:navigation
```

This keeps domain independent, data implementation isolated, presentation UI-focused, and navigation feature-owned.

## Implementation walkthrough

A full feature such as `auth` demonstrates repository contracts in domain and implementations in data.

A medium feature such as `profile` demonstrates preferences-backed state and multiple use cases.

Minimal features such as `home`, `list`, `search`, and `detail` demonstrate the smallest correct structure for routes, ViewModels, and screens.

## Generated features

`scaffoldFeature` creates the same four-module structure automatically. This means new features start with the same architecture as existing features.

## Design trade-offs

This approach creates more modules and more explicit wiring. It is heavier than a small demo app.

The payoff is stronger ownership, smaller test targets, safer refactoring, and consistency across generated features.

## Production checklist

- [ ] each feature has explicit module boundaries
- [ ] domain contains contracts and models
- [ ] data implements domain contracts
- [ ] presentation does not use DTOs directly
- [ ] navigation is feature-owned
- [ ] generated features compile in CI

## Takeaways

- Clean package names are not enough; Gradle boundaries enforce architecture.
- Feature-owned modules scale better than shared buckets.
- Generated features should follow the same rules as hand-written features.
