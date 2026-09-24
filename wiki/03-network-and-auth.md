# 03 - Network and Auth Token Flow

## Selection contract

`core:network` and `feature:auth` are retained only by `remote` and `offline-first` generation. `local` and `minimal` outputs contain neither their paths nor their source, catalog, ProGuard, convention-plugin, documentation, or identifier residue.

The transport layer does not own global connectivity UI. There is no `NetworkMonitor` or app-wide offline banner.

## `core:network`

The module contains the Retrofit/OkHttp transport setup, `BaseRepository`, auth interceptor, token authenticator, network configuration contracts, and tests.

- HTTP logging is `BODY` in debug and `NONE` in release, with sensitive headers redacted.
- Configuration comes from a possibly-empty Hilt set of `NetworkConfigProvider` implementations.
- Without secrets, `DefaultNetworkConfigProvider` uses a non-routable placeholder URL and disables pinning.
- More than one provider is an explicit wiring error.

## Authentication sample

`feature:auth` demonstrates a complete four-module vertical. Its data module supplies Retrofit service wiring, repository logic, request/response models, and the optional token refresher contribution.

For network-backed generated projects:

```text
Splash
├─ onboarding incomplete → Onboarding
└─ onboarding complete
   ├─ stored user exists → Home
   └─ no stored user → Login
```

Completing onboarding enters Login, successful login returns through Splash, and logout clears auth state before returning to Login.

For `local` and `minimal`, the generator removes the entire auth vertical and emits the simpler Onboarding/Home flow.

## Token refresh

`TokenAuthenticator` skips the refresh endpoint, caps retries, synchronizes refresh, double-checks the latest token, and consumes a lazy set of `ITokenRefresher` implementations. The network module never depends on the auth feature; when auth is absent, a 401 is returned without retry.

## Secrets integration

The dependency direction is:

```text
core:network <- core:secrets
```

Secrets may be omitted from network-backed strategies with `-PwithSecrets=false`. Local and minimal omit them automatically because the current secret provider is network configuration infrastructure.

## Known limitation

Routes use kotlinx.serialization while Retrofit uses Gson. This keeps two serialization stacks and corresponding R8 rules in network-backed outputs.

---

[← Previous: 02 - Navigation and UI State](02-navigation-and-ui-state.md) · [Index](README.md) · [Next: 04 - Secrets, Security and Hardening →](04-secrets-and-hardening.md)
