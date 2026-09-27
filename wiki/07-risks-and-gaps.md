# 07 - Risks, Gaps and Open Questions

## Current high-impact risks

1. Navigation state is in-memory and is not restored after process death.
2. Unknown routes render a fallback instead of failing fast in debug.
3. Presentation, navigation, and Compose UI coverage is thinner than infrastructure/build coverage.
4. Retrofit uses Gson while routes use kotlinx.serialization.
5. Broad repository error handling must preserve coroutine cancellation correctly.
6. Theme state remains exposed through the navigation contract.
7. The data-strategy projector relies on exact source/file rewrites; source drift can make projection brittle despite residue checks.
8. `offline-first` names an infrastructure topology, not a generated synchronization architecture.
9. Secret bootstrap is duplicated across CI jobs, and scaffold checks repeat per matrix row.
10. Instrumentation, benchmark execution, coverage reporting, and strict MkDocs validation are absent from PR CI.
11. Branch protection and required checks are repository policy and must be verified/enforced separately.

## Resolved structural risks

- Modules are discovered from the filesystem rather than mirrored in multiple build-file inventories.
- App and library boundaries cover imports and literal project dependencies.
- The plug-out job deletes eight optional paths before rebuilding.
- App-shell connectivity monitoring and the global offline banner were removed.
- Four strategy-specific consumer outputs are generated, residue-checked, and assembled.
- A network-backed secret-free consumer is explicitly proven.
- Invalid strategy values are rejected.

## Load-bearing decisions

### Fixed feature shape

Every retained feature has `data`, `domain`, `navigation`, and `presentation`. Data strategy changes project infrastructure, not feature-layer topology.

### Compile-time projection

Network-free consumers do not contain auth/network types. Valid source flows are emitted during generation rather than guarded by runtime capability checks.

### Module discovery

Settings and app aggregation derive from module folders. Removing a module does not require synchronized edits to a static include or dependency list.

### Optional integrations

Multibindings are used where optional implementations have a real consumer, such as analytics observers and token refreshers.

### Network and secrets scope

`core:network` is transport infrastructure, not global connectivity UI. The secret provider configures that stack, so local/minimal always remove secrets/security; network strategies can retain or omit them.

## Open questions

- Should unknown routes throw in debug builds?
- Should navigation adopt saved-state/process restoration?
- Should Retrofit migrate to kotlinx.serialization?
- Should signing configuration be separated from secret/hardening selection?
- Should the projector move from exact text rewrites toward structured generation/templates?
- What synchronization policy should a real offline-first consumer use?
- Which instrumentation, benchmark, coverage, and docs checks should become required?

## Recommended next work

1. Add focused ViewModel/navigation tests for auth-aware and network-free start flows.
2. Add a strict MkDocs/link check to pull-request CI.
3. Reduce projector brittleness with structured templates or syntax-aware transformations.
4. Define and implement product-specific offline synchronization where needed.
5. Persist navigation state and tighten unknown-route behavior.
6. Consolidate CI bootstrap/scaffold work and add instrumentation/benchmark coverage.
7. Verify and enforce branch-protection policy with required green checks.

---

[← Previous: 06 - Quality, Tests and CI](06-quality-tests-ci.md) · [Index](README.md) · [Next: 08 - Getting Started →](08-getting-started.md)
