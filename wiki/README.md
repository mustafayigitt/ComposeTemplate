# ComposeTemplate Wiki

This wiki is the **single documentation source** for ComposeTemplate. It is written from the source code only — Kotlin/C++ sources, Gradle build logic, the version catalog and the CI workflow on `main`. No previous README, guide or docs tree was used as a reference.

## Pages

| # | Page | What it covers |
| --- | --- | --- |
| 00 | [Project Context](00-project-context.md) | What this repository really is, and the opinions it enforces |
| 01 | [Module Topology and Build System](01-module-topology.md) | ~48 modules, 17 convention plugins, version catalog |
| 02 | [Navigation and UI State](02-navigation-and-ui-state.md) | Hand-written back stack, `ScreenRegistry`, `BaseViewModel` |
| 03 | [Network and Auth Token Flow](03-network-and-auth.md) | `safeCall`, `TokenAuthenticator`, certificate pinning |
| 04 | [Secrets, Security and Hardening](04-secrets-and-hardening.md) | NDK/JNI secret pipeline and Gradle guardrails |
| 05 | [Generator and Scaffolding Tooling](05-generator-and-scaffolding.md) | `scaffoldFeature`, `create-new-app` |
| 06 | [Quality, Tests and CI](06-quality-tests-ci.md) | Unit tests, four CI jobs, template smoke test |
| 07 | [Risks, Gaps and Open Questions](07-risks-and-gaps.md) | 17 findings with a remediation order |
| 08 | [Getting Started](08-getting-started.md) | Commands and secret keys taken from the build |

Reading order: start with 00 for the mental model, then 01–06 for subsystems, and read 07 before making changes.

## Source of truth

| Aspect | Value |
| --- | --- |
| Repository | `mustafayigitt/ComposeTemplate` |
| Branch inspected | `main` |
| Base package | `com.ytapps.composetemplate` |
| License | Apache-2.0 |
| Gradle modules | ~48 |
| Convention plugins | 17 |
| minSdk / targetSdk / compileSdk | 23 / 36 / 37 |
| Kotlin / AGP / KSP | 2.0.21 / 9.2.1 / 2.0.21-1.0.28 |

## One-paragraph summary

ComposeTemplate is not a sample app: it is a **Gradle-based project generator** whose own application code doubles as the live fixture that proves the generator works. The heaviest logic sits in `build-logic/convention` (feature scaffolding, app rebranding, secret validation), and the most opinionated runtime code sits in `core:navigation` (a hand-written back stack over Navigation3) and `core:secrets` (NDK/JNI secret obfuscation with dynamic `RegisterNatives`). The template's guardrails — secret validation, APK/AAB secret scanning, and a CI job that generates a feature and a whole new app and then compiles them — are its real differentiator.
