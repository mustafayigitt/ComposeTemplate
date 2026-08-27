# Feature Scaffolding for Clean Architecture

## Who this article is for

This article is for Android developers who want to generate feature boilerplate without weakening architecture.

## What you will learn

- why feature scaffolding matters in modular projects
- what ComposeTemplate generates
- how scaffolding supports Clean Architecture
- why generated code is a starting point, not final product code

## The problem

Creating a feature in a modular Clean Architecture project requires many repeated steps: modules, Gradle plugins, package directories, route classes, UI state, ViewModel, screen provider, Hilt binding, app wiring, and optional persistence files.

Manual setup is slow and easy to get wrong.

## Why this matters for Android projects

When feature creation is inconsistent, architecture erodes. Developers copy from random modules, forget bindings, or skip tests.

A scaffold keeps the first commit of a feature aligned with project conventions.

## ComposeTemplate's approach

ComposeTemplate provides:

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

With database starter files:

```bash
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

The task creates:

```text
feature/settings/
├── data
├── domain
├── navigation
└── presentation
```

## Generated pieces

The scaffold includes route, use case, `UiState`, `Event`, ViewModel, screen, screen provider, Hilt module, string resources, Gradle files, and optional Room entity/DAO.

It also updates `settings.gradle.kts` and `app/build.gradle.kts`.

## Design trade-offs

Generated code is intentionally minimal. It should compile, show the correct structure, and give developers a safe starting point.

It should not pretend to understand feature-specific business logic.

## Production checklist

- [ ] scaffold creates all required modules
- [ ] generated modules use convention plugins
- [ ] app/settings wiring is updated
- [ ] generated feature compiles
- [ ] placeholder behavior is replaced
- [ ] tests are added after real behavior is implemented

## Takeaways

- Scaffolding protects architecture at feature creation time.
- Generated code should be minimal and correct.
- CI should compile scaffolded output.
