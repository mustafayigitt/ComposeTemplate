# Feature Modularization with Clean Architecture in Android

Feature modularization is one of the core architectural decisions in ComposeTemplate. Instead of putting all feature code into one module or relying only on package names, each feature is split into explicit Gradle modules.

## The problem

As Android apps grow, feature code tends to mix concerns. UI code starts using network DTOs, repository implementations leak into presentation, navigation becomes centralized, and feature ownership becomes unclear.

Folders alone do not prevent this. Gradle module boundaries make the separation visible and enforceable.

## ComposeTemplate’s feature structure

```text
feature/{name}/
├── data/
├── domain/
├── navigation/
└── presentation/
```

| Module | Responsibility |
|---|---|
| `data` | Repository implementation, DTOs, API/DB access |
| `domain` | Use cases, domain models, repository contracts |
| `navigation` | Routes, navigation items, bottom-bar contracts |
| `presentation` | ViewModels, UiState/Event, Compose screens |

## Dependency direction

```text
data → domain ← presentation
presentation → navigation
navigation → core:navigation
```

## Takeaway

Feature modularization is not about creating folders. It is about making architectural boundaries visible at build time.
