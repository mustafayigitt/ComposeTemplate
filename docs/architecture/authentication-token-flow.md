# Authentication and Token Flow

Authentication in ComposeTemplate is modeled as a collaboration between the network layer, preferences, and the auth feature.

The template provides the infrastructure shape; generated applications must connect it to their real backend contract.

## Problem

Authentication logic becomes fragile when every feature handles tokens independently.

Common issues include:

- duplicated token injection,
- access tokens logged in debug output,
- refresh behavior implemented inconsistently,
- retry loops after 401 responses,
- UI code knowing persistence details,
- refresh tokens stored or cleared inconsistently.

ComposeTemplate keeps token concerns behind shared infrastructure and domain contracts.

## Main pieces

| Piece | Responsibility |
|---|---|
| `AuthInterceptor` | attaches auth-related request data |
| `ITokenRefresher` | shared contract for refreshing access tokens |
| `IAuthRepository` | auth feature domain contract |
| `AuthRepository` | auth data implementation |
| `IPreferencesManager` | stores access token, refresh token, token type, user state |
| `BaseRepository` | maps expected network failures into result values |

## Login flow

A typical login flow is:

```text
Login screen
  -> LoginViewModel
  -> LoginUseCase
  -> IAuthRepository.login
  -> AuthRepository
  -> AuthService.login
  -> map response to AuthModel
  -> store access token, refresh token, token type
  -> return Result<AuthModel>
```

The ViewModel does not know how tokens are stored or how the API call is made.

## Token refresh contract

`IAuthRepository` extends `ITokenRefresher`, allowing shared network infrastructure to request a fresh token without depending directly on the data implementation class.

This keeps the network layer dependent on a contract rather than feature internals.

## Preference storage

The auth repository stores successful login results through `IPreferencesManager`:

- access token,
- refresh token,
- token type.

This centralizes token state and avoids scattering storage logic across ViewModels or screens.

## Error handling

Authentication API calls use `BaseRepository.safeCall`, which maps expected HTTP and IO failures into explicit `Result` values.

This lets UI state respond to errors without catching low-level networking exceptions directly.

## Security considerations

Token handling should follow these rules:

- do not log access tokens,
- do not log refresh tokens,
- redact sensitive headers,
- keep refresh behavior centralized,
- avoid infinite retry loops,
- treat client-side storage as extractable on compromised devices,
- make backend authorization the final security boundary.

## Generated app responsibility

ComposeTemplate provides the structure and starter flow, but each generated app must implement real backend details:

- endpoint paths,
- request/response models,
- token expiration rules,
- logout behavior,
- refresh failure handling,
- account/session invalidation policy.

## Checklist

- [ ] token injection is centralized.
- [ ] token refresh goes through a shared contract.
- [ ] ViewModels do not read/write tokens directly.
- [ ] tokens are not logged.
- [ ] auth failures are represented explicitly.
- [ ] generated apps adapt the starter flow to the real backend contract.

## Repository references

- `feature/auth/domain/IAuthRepository.kt`
- `feature/auth/domain/LoginUseCase.kt`
- `feature/auth/data/AuthRepository.kt`
- `core/common/ITokenRefresher`
- `core/data/IPreferencesManager`
- `core/network`
