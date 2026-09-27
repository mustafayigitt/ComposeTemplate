# 03 - Network and Auth Token Flow

## Selection contract

`core:network` and `feature:auth` are retained only for `remote` and `offline-first`. `local` and `minimal` remove their modules and associated catalog, ProGuard, convention, documentation, navigation, and identifier residue.

`offline-first` selects both network/auth and Room infrastructure. The template does not generate synchronization, retry queues, conflict resolution, cache policy, or background reconciliation.

## `core:network`

The module contains Retrofit/OkHttp setup, `BaseRepository`, the auth interceptor, `TokenAuthenticator`, network configuration contracts, and unit tests.

- HTTP logging is `BODY` in debug and `NONE` in release, with sensitive headers redacted.
- Configuration comes from a possibly empty Hilt set of `NetworkConfigProvider` implementations.
- A secret-free network consumer uses a non-routable placeholder base URL and disables pinning until product configuration is supplied.
- Multiple configuration providers are treated as a wiring error.
- The module does not own global connectivity UI.

## Authentication sample

`feature:auth` is a complete four-layer vertical. Its data layer provides Retrofit service wiring, repository logic, models, and an optional token refresher contribution.

For network-backed generated projects:

```text
Splash
├─ onboarding incomplete → Onboarding
└─ onboarding complete
   ├─ stored user exists → Home
   └─ no stored user → Login
```

For `local` and `minimal`, the generator removes the auth vertical and emits Onboarding/Home flow with no `LoginRoute` reference.

## Token refresh

`TokenAuthenticator` skips the refresh endpoint, caps retries, synchronizes refresh, double-checks the newest token, and consumes a lazy set of `ITokenRefresher` implementations. `core:network` never depends on the auth feature; when no refresher exists, a 401 is returned without retry.

## Secrets dependency direction

```text
core:network <- core:secrets
```

Secrets contribute network configuration and may be omitted from `remote` or `offline-first` with `-PwithSecrets=false`. Local/minimal remove secrets and security automatically.

A network-backed consumer without secrets must replace the placeholder configuration with product-owned runtime/build configuration before contacting a real backend.

## Serialization split

Routes use kotlinx.serialization while Retrofit uses Gson. Network-backed outputs therefore retain two serialization stacks and their R8 configuration.

---

[← Previous: 02 - Navigation and UI State](02-navigation-and-ui-state.md) · [Index](README.md) · [Next: 04 - Secrets, Security and Hardening →](04-secrets-and-hardening.md)
