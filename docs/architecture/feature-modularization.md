# Feature Modularization

Feature modularization is one of ComposeTemplate’s most important architectural decisions.

## Structure

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

## Checklist

- [ ] Repository contract is in domain.
- [ ] Repository implementation is in data.
- [ ] Route is owned by navigation.
- [ ] ScreenProvider is owned by presentation.
- [ ] App wires feature modules explicitly.
