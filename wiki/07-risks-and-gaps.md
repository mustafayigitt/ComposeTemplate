# 07 - Risks, Gaps and Open Questions

Findings from reading the code, ordered by practical impact. Each item is an observation with a concrete location, not a style opinion.

## High impact

| # | Finding | Where | Why it matters |
| --- | --- | --- | --- |
| 1 | Scaffolding mutates build files by text anchor and fails silently | `ScaffoldFeaturePlugin` | If the last `implementation(project(":feature:` line in `app/build.gradle.kts` is missing or reformatted, the task logs `not changed` and still succeeds, producing a feature the app never includes |
| 2 | Navigation back stack is not persisted | `NavigationManager` | A `@Singleton` in-memory `MutableStateFlow<List<INavigationItem>>` is lost on process death; users return to the start destination with no state restoration |
| 3 | Unresolved routes render text instead of failing | `ScreenRegistry` | Fallback shows `"Screen not found: <route>"`; a DI wiring mistake ships as a broken screen rather than a build/test failure |
| 4 | Test pyramid inverted | whole repo | 5 unit tests, all infrastructure; 8 presentation modules and the `BaseViewModel` contract are untested |

## Medium impact

| # | Finding | Where | Why it matters |
| --- | --- | --- | --- |
| 5 | Two serialization stacks | `NetworkModule` (Gson) vs `kotlin.serialization` for routes/models | Extra dependency surface, reflection, and ProGuard-keep burden for no functional gain |
| 6 | `safeCall` catches only `HttpException` and `IOException` | `BaseRepository` | Deserialization or `IllegalState` failures escape the `Result` abstraction and can crash callers that assume total coverage |
| 7 | Theme state exposed through the navigation contract | `INavigationManager.isDarkModeFlow` | Misplaced responsibility; consumers depend on navigation to read appearance settings |
| 8 | CI secret bootstrap duplicated four times | `.github/workflows/ci.yml` | Drift risk; a reusable workflow or composite action would collapse it into one definition |
| 9 | No module-graph enforcement | build-logic | Layer rules live in tier plugins and review only; nothing fails the build when a boundary is crossed or a feature imports another feature |
| 10 | `SecretManager` is a global object requiring `initialize(context)` | `core:secrets` | Initialization-order coupling; a secret read before startup completes fails at runtime rather than compile time |

## Lower impact / by design but worth stating

| # | Finding | Note |
| --- | --- | --- |
| 11 | Bottom-bar order derives from multibinding key strings | Ordering is a naming convention, not a typed contract |
| 12 | Fixed 4-module shape for every feature | Consistent, but trivial features (`splash`, `detail`) pay full configuration cost — 32 modules for 8 features |
| 13 | Unbuffered event `Channel` in `BaseViewModel` | Single-consumer, rendezvous semantics; needs to be documented for feature authors |
| 14 | `runBlocking` inside `TokenAuthenticator` | Required by OkHttp's blocking `Authenticator` API; rationale is in the source |
| 15 | Certificate pinning disabled in debug | Pin misconfiguration only appears in release builds |
| 16 | `config` module is local-only | `IConfigManager` + `LocalConfigProvider`; no remote-config backend, so runtime flags require a release |
| 17 | Benchmarks and baseline profiles never run in CI | Performance infrastructure exists but is unmeasured |

## Suggested remediation order

1. Make `scaffoldFeature` fail loudly when it cannot wire `settings.gradle.kts` or `app/build.gradle.kts` (item 1).
2. Persist the navigation back stack and make unresolved routes fail in debug builds (2, 3).
3. Add ViewModel tests for at least one full feature vertical and a `ScreenRegistry` coverage test (4, 3).
4. Pick one serialization stack; add a broad `catch` to `safeCall` (5, 6).
5. Move `isDarkModeFlow` to a theme/preferences contract (7).
6. Extract the CI secret bootstrap into a composite action (8).

## Open questions for the maintainer

- Is the fixed 4-module feature shape intended to be non-negotiable, or should `scaffoldFeature` support a lighter UI-only variant?
- Should `ScreenRegistry` throw in debug builds and fall back only in release?
- Is Gson kept deliberately (backend contract flexibility) or is it legacy?
- Should `hardeningReport` and `scanApkForSecrets` be part of the CI release job rather than manual tasks?

---

[← Previous: 06 - Quality, Tests and CI](06-quality-tests-ci.md) · [Index](README.md) · [Next: 08 - Getting Started →](08-getting-started.md)
