# Clean Architecture

ComposeTemplate applies Clean Architecture through Gradle module boundaries, not only through package names.

The goal is to make invalid dependencies difficult to introduce accidentally.

## Problem

A project can look clean while still being tightly coupled. Packages named `data`, `domain`, and `presentation` are useful, but if they live in the same module, the compiler cannot prevent UI code from importing DTOs or domain code from depending on framework details.

ComposeTemplate treats architecture as a build-level constraint.

## Dependency rule

```text
Data can know Domain.
Presentation can know Domain.
Domain should not know Data or Presentation.
```

Simplified:

```text
feature:{name}:data          -> feature:{name}:domain
feature:{name}:presentation  -> feature:{name}:domain
feature:{name}:domain        -> core:common
```

## Layer responsibilities

| Layer | Owns | Should avoid |
|---|---|---|
| Domain | business models, repository contracts, use cases | Retrofit, Room, Compose, Android UI |
| Data | repository implementations, DTOs, services, mappers | Composables, UI state, navigation rendering |
| Presentation | ViewModels, UI state, events, Compose screens | DTOs, Retrofit services, DAOs |
| Navigation | route objects and navigation metadata | business logic and screen internals |

## Repository contracts

Repository interfaces live in domain:

```kotlin
interface IAuthRepository {
    fun hasUser(): Boolean

    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthModel>
}
```

Repository implementations live in data:

```kotlin
internal class AuthRepository(
    private val authService: AuthService,
    private val prefs: IPreferencesManager,
) : BaseRepository(), IAuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<AuthModel> {
        return safeCall {
            authService.login(AuthRequestModel(email, password))
        }.map { response ->
            AuthModel(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn,
                tokenType = response.tokenType,
            )
        }
    }
}
```

This lets Presentation depend on abstractions instead of network implementations.

## DTOs and domain models

DTOs describe transport details. Domain models describe what the app uses internally.

A feature data module may receive an API response with network-specific names and shapes. The repository maps that response into a domain model before returning it.

This protects the rest of the app from backend response changes.

## Result modeling

ComposeTemplate uses explicit result values from `core:common` instead of letting network exceptions leak through Presentation.

`BaseRepository.safeCall` converts expected network failures into `Result.Error` and successful responses into `Result.Success`.

Expected failures include:

- empty response body,
- unauthorized response,
- forbidden response,
- not found response,
- server error response,
- `HttpException`,
- `IOException`.

It intentionally avoids catching generic `Exception` by default. Broad catches can hide mapper bugs, serialization issues, nullability mistakes, and other programming errors that should fail during development.

## Use cases

Use cases sit in domain and give Presentation a stable API for business actions.

```kotlin
class LoginUseCase(
    private val authRepository: IAuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<AuthModel> = authRepository.login(email, password)
}
```

Simple use cases may delegate directly. More complex use cases can validate input, combine repositories, or orchestrate multiple calls.

## Gradle boundaries

A domain module should be lean:

```kotlin
plugins {
    id("composetemplate.feature.domain")
}
```

A data module depends on its domain module:

```kotlin
dependencies {
    implementation(project(":feature:auth:domain"))
}
```

A presentation module depends on domain and navigation:

```kotlin
dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:navigation"))
}
```

## Common mistakes

### UI imports DTOs

DTOs belong in Data. UI state should use domain models or display-ready values.

### Domain imports framework details

Domain should not know about Retrofit, Room, Compose, Android `Context`, or screen rendering.

### ViewModels depend on repository implementations

Prefer depending on use cases or domain contracts.

### Catching every exception

Do not turn every unexpected programming error into a generic failure result.

## Trade-offs

Clean Architecture adds files, modules, and mapping code. For a tiny application, that can feel heavy.

ComposeTemplate accepts this because it is meant to be a production-grade template generator. The benefit is that generated applications start with boundaries that can survive growth.

## Checklist

- [ ] repository contracts live in domain.
- [ ] repository implementations live in data.
- [ ] DTOs are mapped before reaching Presentation.
- [ ] Domain is independent of Android UI and infrastructure frameworks.
- [ ] Presentation depends on use cases or domain contracts.
- [ ] expected failures are represented explicitly.
- [ ] broad exception swallowing is avoided.

## Repository references

- `feature/auth/domain`
- `feature/auth/data`
- `core/common`
- `core/network/BaseRepository.kt`
- `build-logic/convention/FeatureDomainConventionPlugin.kt`
- `build-logic/convention/FeatureDataConventionPlugin.kt`
