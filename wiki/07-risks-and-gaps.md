# 07 - Risks, Gaps and Open Questions

## Current high-impact risks

1. Navigation back stack is in-memory and is not restored after process death.
2. `ScreenRegistry` renders a fallback message for unknown routes instead of failing in debug.
3. Presentation and Compose UI coverage remains thin compared with infrastructure tests.
4. Retrofit uses Gson while navigation uses kotlinx.serialization.
5. `BaseRepository.safeCall` does not map every unexpected failure; any broad catch must rethrow coroutine cancellation.
6. Theme state is still exposed through the navigation contract.
7. Secret bootstrap is duplicated across CI jobs.
8. Benchmarks and baseline profiles are removable but are not executed in CI.
9. The main branch is not protected by required checks.

## Resolved structural risks

- Modules are discovered from the filesystem instead of mirrored in settings and app build files.
- App and library boundary checks cover imports and literal project dependencies.
- Performance, analytics, database, network, secrets/security, and auth removal are exercised by build or generation checks.
- App-shell connectivity monitoring and the global offline banner were removed, so network-free output no longer carries always-on connectivity behavior.
- `dataStrategy` now projects four residue-checked consumer configurations.

## Load-bearing decisions

### Fixed feature shape

Every retained feature has exactly `data`, `domain`, `navigation`, and `presentation`. Data strategy changes infrastructure, not feature-layer topology.

### Generated selection, not runtime capability checks

A local/minimal project does not contain auth or network types. The generator therefore emits different valid source flows rather than checking at runtime whether a module exists.

### Module discovery

`settings.gradle.kts` scans for module build files, and `:app` wires discovered core/feature projects. Deleting a module folder does not require a mirrored include/dependency edit.

### Optional integrations

Multibindings are used where there is a real optional consumer, such as analytics observers and token refreshers. A generic app-chrome/capability graph was rejected because there was no second UI consumer.

### Network scope

`core:network` is transport only. Connectivity monitoring and global UI are not part of the retained shell. Feature-specific connectivity UX belongs to the feature that needs it.

### Secrets interaction

The current secret provider configures the network stack. Consequently local/minimal output removes secrets/security even when `withSecrets` is omitted. Network strategies may retain or omit it explicitly.

## Open questions

- Should unknown routes throw in debug builds?
- Should the navigation stack use saved state/process restoration?
- Should Retrofit migrate from Gson to kotlinx.serialization?
- Should signing be separated from secret/hardening configuration?
- Should CI run at least one instrumentation and benchmark scenario?

## Recommended next work

1. Stabilize and merge the four-strategy generation matrix.
2. Add ViewModel coverage for auth-aware and network-free start flows.
3. Persist navigation state and tighten unknown-route behavior.
4. Unify serialization and broaden repository error mapping safely.
5. Extract reusable CI setup.

---

[← Previous: 06 - Quality, Tests and CI](06-quality-tests-ci.md) · [Index](README.md) · [Next: 08 - Getting Started →](08-getting-started.md)
