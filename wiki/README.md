# ComposeTemplate Wiki

This wiki documents the current source, build logic, generator, and CI contract of ComposeTemplate. ComposeTemplate is a **CI-proven Jetpack Compose project generator**; the application in this repository is the living fixture used to prove generated consumers.

## Pages

| # | Page | What it covers |
| --- | --- | --- |
| 00 | [Project Context](00-project-context.md) | Product model, enforced opinions, scale, and scope |
| 01 | [Module Topology and Build System](01-module-topology.md) | 47 modules, 21 convention plugins, discovery, boundaries, toolchain |
| 02 | [Navigation and UI State](02-navigation-and-ui-state.md) | Navigation3 back stack, registry, observers, generated start flows |
| 03 | [Network and Auth Token Flow](03-network-and-auth.md) | Transport, auth, token refresh, and strategy selection |
| 04 | [Secrets, Security and Hardening](04-secrets-and-hardening.md) | Optional NDK/JNI obfuscation and Gradle guardrails |
| 05 | [Generator and Scaffolding Tooling](05-generator-and-scaffolding.md) | Four data strategies, projection, residue checks, feature scaffolding |
| 06 | [Quality, Tests and CI](06-quality-tests-ci.md) | Local gates, five job definitions, eight checks, generation matrix |
| 07 | [Risks, Gaps and Open Questions](07-risks-and-gaps.md) | Current limitations and recommended next work |
| 08 | [Getting Started](08-getting-started.md) | Generation, verification, scaffolding, and release checklist |

Recommended order: read 00 for the mental model, 01–06 for subsystem contracts, 07 before architectural changes, and 08 when generating a consumer.

## Current source snapshot

| Aspect | Value |
| --- | --- |
| Repository | `mustafayigitt/ComposeTemplate` |
| Base package | `com.ytapps.composetemplate` |
| License | Apache-2.0 |
| Gradle modules | 47 |
| Android library modules | 44 |
| Convention plugins | 21 |
| Data strategies | `remote`, `offline-first`, `local`, `minimal` |
| minSdk / targetSdk / compileSdk | 26 / 36 / 37 |
| Kotlin / AGP / KSP | 2.0.21 / 9.2.1 / 2.0.21-1.0.28 |

## Generator contract in one paragraph

`create-new-app` copies consumer files into a sibling project, rebrands package and application names, applies one strict data strategy, optionally retains network-backed secrets/hardening, removes generator-only/template-only content, and rejects forbidden residue. Every retained feature still has `data`, `domain`, `navigation`, and `presentation`; the strategy changes only project-level infrastructure. CI proves all four generated strategies, debug and release assembly, route projection, residue cleanup, feature scaffolding, and a secret-free `remote` consumer.

## Scope boundary

The generator supplies architecture, build conventions, selected infrastructure, example features, and executable checks. A generated app still needs product-specific domain behavior, backend contracts, branding, release signing, migrations, and runtime/UI tests.

`offline-first` currently means that network/auth and Room infrastructure coexist. It does **not** generate synchronization, queues, conflict resolution, cache policy, or background reconciliation.
