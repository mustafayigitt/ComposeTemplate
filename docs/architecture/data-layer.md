# Data Layer

The data layer owns remote API access, local persistence, repository implementations, DTOs, and infrastructure mapping.

ComposeTemplate keeps data concerns outside Presentation and behind Domain contracts.

## Problem

When data access is not isolated, application code tends to leak infrastructure details across layers:

- API response DTOs appear in UI state,
- ViewModels call Retrofit services directly,
- token persistence is duplicated,
- error handling differs by feature,
- local preferences and database access become scattered.

ComposeTemplate centralizes shared data infrastructure in `core` and feature-specific data behavior in feature data modules.

## Shared infrastructure

| Module | Responsibility |
|---|---|
| `core:data` | preferences, token storage, locale/theme persistence |
| `core:database` | Room foundation |
| `core:network` | Retrofit, OkHttp, auth infrastructure, `BaseRepository` |
| `core:secrets` | API key and base URL access |
| `core:common` | result type and shared contracts |

## Feature data modules

A feature data module owns that feature's concrete data implementation:

```text
feature/auth/data/
├── remote/
├── model/
├── di/
└── AuthRepository.kt
```

The data module can use infrastructure modules, but it should expose behavior through domain contracts.

## Repository implementation

The auth feature demonstrates the pattern:

- `IAuthRepository` lives in `feature/auth/domain`.
- `AuthRepository` lives in `feature/auth/data`.
- `AuthRepository` uses `AuthService`, `IPreferencesManager`, and `BaseRepository`.
- API responses are mapped into `AuthModel` before returning.

This keeps Presentation independent from Retrofit and persistence details.

## BaseRepository

`BaseRepository.safeCall` standardizes expected network response handling.

It maps:

- successful responses with body to `Result.Success`,
- empty response bodies to `Result.Error`,
- common HTTP errors to readable error messages,
- `HttpException` and `IOException` to `Result.Error`.

It does not catch generic `Exception` by default, so programming errors are not silently swallowed.

## Persistence responsibilities

Preferences and local state are centralized through `core:data`. The profile feature uses this for theme and language behavior. Auth uses it to store access and refresh tokens.

Sensitive data handling should be intentional. Do not treat regular preferences as a secure vault.

## DTO mapping

DTOs should stay inside Data. Domain and Presentation should work with domain models or UI models.

Mapping is where transport-specific names and shapes are translated into app-facing models.

## Checklist

- [ ] feature repository implementations live in data modules.
- [ ] repository contracts live in domain modules.
- [ ] DTOs do not leak into Presentation.
- [ ] expected network errors are mapped consistently.
- [ ] token and preference access is centralized.
- [ ] sensitive data is not treated as secure just because it is local.
- [ ] Data modules depend inward on Domain, not the other way around.

## Repository references

- `core:data`
- `core:database`
- `core:network`
- `core:secrets`
- `feature/auth/data/AuthRepository.kt`
- `feature/auth/domain/IAuthRepository.kt`
