# 07 - Risks, Gaps and Open Questions

## Resolved in the architecture hardening change

- Navigation persists stable route strings and restores typed back stacks after Activity/process recreation.
- Unknown routes and ambiguous restorers fail fast.
- Navigation no longer exposes theme state or depends on `core:data`.
- Retrofit DTOs and routes use kotlinx.serialization; Gson was removed from the active stack.
- `BaseRepository.safeCall` has an explicit cancellation-propagation test.
- Existing and newly scaffolded screen providers restore routes.
- Data-strategy rewrites require exactly one source match and have build-logic tests for success, missing, and duplicate matches.
- Network-free projection removes the kotlinx.serialization Retrofit converter and validates its absence.
- CI secret setup is centralized, scaffold checks are deduplicated, MkDocs is strict, Jacoco reports are uploaded, and benchmark instrumentation executes on an emulator.

## Explicit scope boundaries

### Offline-first

`offline-first` means network/auth and Room infrastructure are retained together. The template does not invent synchronization, conflict, retry, cache, or reconciliation policy; those decisions belong to the generated product.

### Fixed feature shape

Every retained feature has `data`, `domain`, `navigation`, and `presentation`. Data strategy changes infrastructure, not feature-layer topology.

### Compile-time projection

Network-free consumers contain no auth/network types. The generator emits valid source rather than runtime capability branches.

## Remaining risks

1. Product-specific presentation and Compose UI behavior still needs product-specific tests beyond template smoke coverage.
2. Exact-count projection is safer and drift-detecting, but structured templates would reduce long-term coupling further.
3. Signing configuration remains coupled to the selected secret/hardening subsystem.
4. Branch protection is still not enabled on `main`; repository policy must require pull requests, green checks, an up-to-date branch, and optionally one approval.

## Next work

- Expand product-owned UI and end-to-end restoration coverage.
- Consider structured generation instead of source text projection.
- Decide whether signing and runtime hardening should be independently selectable.
- Enable branch protection/rulesets in repository settings.

---

[← Previous](06-quality-tests-ci.md) · [Index](README.md) · [Next →](08-getting-started.md)
