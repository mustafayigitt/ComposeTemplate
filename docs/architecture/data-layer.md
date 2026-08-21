# Data Layer

The data layer owns remote API access, local persistence, and repository implementations.

## Shared infrastructure

- `core:data`: preferences and locale handling
- `core:database`: Room foundation
- `core:network`: Retrofit, OkHttp, BaseRepository, auth components

## Feature data module

A feature data module owns that feature’s repository implementation, DTOs, mappers, and local/remote source integration.

## Checklist

- [ ] DTOs are separate from domain models.
- [ ] Repository interface is in domain.
- [ ] Implementation is in data.
- [ ] Sensitive data is not stored as regular preferences.
