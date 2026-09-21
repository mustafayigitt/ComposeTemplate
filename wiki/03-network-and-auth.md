# 03 - Network and Auth Token Flow

## `core:network` contents

`AuthInterceptor.kt`, `TokenAuthenticator.kt`, `BaseRepository.kt`, `NetworkConfigProvider.kt`, `DefaultNetworkConfigProvider.kt`, `di/NetworkModule.kt`, `di/NetworkConfigModule.kt`, `di/TokenRefresherModule.kt`. Tests: `BaseRepositoryTest`, `TokenAuthenticatorTest`.

> `NetworkMonitor` used to live here. It now sits in `core:common` (`core.common.connectivity`) because it only observes `ConnectivityManager` and never touches Retrofit or OkHttp. Connectivity is a device capability; `core:network` is the transport layer and stays optional.

## `NetworkModule`

- `@InstallIn(SingletonComponent)` object.
- OkHttp client composed of `AuthInterceptor` + `TokenAuthenticator` + `HttpLoggingInterceptor`.
- Logging level `BODY` in debug, `NONE` in release; redacts `Authorization`, `Cookie`, `Set-Cookie` headers.
- Retrofit and certificate pinning read their settings through `NetworkConfigProvider`; `core:network` does not import or depend on `SecretManager`.
- `NetworkConfigModule` declares an empty Hilt multibinding set. Optional infrastructure can contribute one provider without making the transport layer depend on that infrastructure.
- With no contribution, `DefaultNetworkConfigProvider` keeps the project buildable with `https://example.invalid/` and certificate pinning disabled. The endpoint is intentionally non-routable and must be replaced before real API use.
- More than one provider is treated as an ambiguous wiring error and fails explicitly.
- Certificate pinning is **skipped** when `BuildConfig.DEBUG` or when the selected provider disables it; when active it requires at least `MIN_CERTIFICATE_PIN_COUNT = 2` pins and applies them to the host parsed from the provider's base URL.
- Retrofit uses `GsonConverterFactory`.

### Optional secrets integration

`core:secrets` depends on `core:network`, not the reverse. It contributes `SecretNetworkConfigProvider` through `@Binds @IntoSet`; that adapter reads the base URL and pinning settings from `SecretManager`.

This direction is what lets a generated project omit secrets and native hardening without breaking the network module:

```text
core:network <- core:secrets
```

> **Warning:** The project applies `kotlin.serialization` for navigation routes and models while the HTTP layer converts JSON with Gson. Two serialization stacks coexist; unifying on kotlinx.serialization would remove Gson reflection and ProGuard-keep pressure.

## `BaseRepository.safeCall`

- Wraps a Retrofit `Response` into `Result.Success` / `Result.Error` (`Result` lives in `core:common`).
- Maps `401`, `403`, `404`, and the `500..599` range (`SERVER_ERROR_START = 500`, `SERVER_ERROR_END = 599`) to error states.
- Catches `HttpException` and `IOException`.
- Does **not** catch generic exceptions, so deserialization or `IllegalState` failures propagate to the caller.

## `TokenAuthenticator` — the most interesting piece

An OkHttp `Authenticator` (not an interceptor), `@Singleton internal`, injected with `IPreferencesManager` and `Lazy<Set<ITokenRefresher>>`.

Behavior:

1. Requests to `AUTH_REFRESH_PATH = "/auth/refresh"` are skipped so refresh cannot recurse.
2. Retry depth is counted through the `priorResponse` chain, capped at `MAX_RETRY_COUNT = 3`.
3. `synchronized(lock)` with a double-check of the latest stored token: if another thread already refreshed, the request is simply retried with the new token instead of refreshing again.
4. Refresh itself runs under `runBlocking`, with an in-code rationale (OkHttp's `Authenticator` API is blocking).
5. The retried request is rebuilt with `"$tokenType $token"`, defaulting to `Bearer`.

### Why the refresher is an optional `Set`

`ITokenRefresher` is declared in `core:common` and implemented in `feature:auth:data`. `core:network` therefore never depends on a feature module.

The set is declared with `@Multibinds` in `TokenRefresherModule`, so a project that deletes `feature:auth` still builds: `resolveRefresher()` returns `null`, a 401 is simply not retried, and Timber logs `"No token refresher is installed; a 401 will not be retried."`. When exactly one refresher is present it is used; more than one is a wiring error and fails loudly.

`dagger.Lazy` around the set is not cosmetic — without it the `AuthRepository → Retrofit → OkHttpClient → TokenAuthenticator` construction cycle returns. This is the cleanest example of dependency inversion in the codebase and is worth keeping intact when features change.

## Feature-side example: `feature:auth:data`

`AuthRepository`, `remote/AuthService`, `model/{AuthRequestModel, AuthResponseModel, RefreshTokenRequestModel}`, `di/BinderModule` (binds interfaces, including `@Binds @IntoSet` for `ITokenRefresher`) and `di/ProviderModule` (provides Retrofit service). `AuthRepositoryTest` covers the repository path.

> **Note:** The refresh endpoint in the template is a placeholder shape — a generated project must point it at a real backend contract before release.

---

[← Previous: 02 - Navigation and UI State](02-navigation-and-ui-state.md) · [Index](README.md) · [Next: 04 - Secrets, Security and Hardening →](04-secrets-and-hardening.md)
