# 03 - Network and Auth Token Flow

## Scope

`core:network` and `feature:auth` are retained for `remote` and `offline-first`; `local` and `minimal` remove them and their catalog, build, ProGuard, navigation, and documentation residue.

`offline-first` is an infrastructure topology: network/auth plus Room. Synchronization, retry queues, conflict resolution, cache policy, and background reconciliation remain product-specific and are intentionally not generated.

## Network and serialization

Retrofit uses the kotlinx.serialization converter with a configured `Json` instance. Auth requests and responses are `@Serializable` and use `@SerialName` where wire names differ. Routes use the same serialization family, removing the former Gson/kotlinx split.

`BaseRepository.safeCall` rethrows `CancellationException`; tests explicitly protect coroutine cancellation propagation.

## Authentication and refresh

Network-backed projects route completed onboarding to Home when a stored user exists and Login otherwise. `TokenAuthenticator` skips refresh calls, caps retries, serializes refresh, rechecks the newest token, and consumes optional `ITokenRefresher` contributions without making `core:network` depend on auth.

Secrets may be omitted from a network strategy with `-PwithSecrets=false`; such consumers receive placeholder network configuration that must be replaced before production use.

---

[← Previous](02-navigation-and-ui-state.md) · [Index](README.md) · [Next →](04-secrets-and-hardening.md)
