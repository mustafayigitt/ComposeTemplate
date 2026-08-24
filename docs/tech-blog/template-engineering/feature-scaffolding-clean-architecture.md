# Feature Scaffolding for Clean Architecture

Feature scaffolding reduces the repetitive work of creating new Clean Architecture feature modules.

## Problem

Adding a feature manually means creating modules, Gradle files, routes, ViewModels, UiState/Event classes, Hilt modules, and app wiring. Missing one step can break the build.

## ComposeTemplate approach

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

## Generated structure

```text
feature/settings/
├── data/
├── domain/
├── navigation/
└── presentation/
```

## Takeaway

Scaffolding makes the intended architecture easy to follow.
