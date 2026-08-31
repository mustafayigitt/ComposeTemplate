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
| 8 | CI secret bootstrap duplicated five times | `.github/workflows/ci.yml` | Drift risk; a reusable workflow or composite action would collapse it into one definition |
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

## Baseline decision log

Decisions that constrain the whole template and are easy to reverse accidentally.

### `minSdk` is 26 (Android 8.0, Oreo)

The template previously shipped `minSdk 23`. That number was never load-bearing:
the repository contains no `@RequiresApi` or `@TargetApi` annotation, and all three
runtime `SDK_INT` checks already guard higher levels — `Build.VERSION_CODES.S` (31)
for dynamic color in `Theme.kt`, and `Build.VERSION_CODES.P` (28) twice in
`DeviceIntegrityManager` for `signingInfo`. Raising the baseline therefore required
zero source changes and removed zero code.

What the Oreo baseline buys:

| Available without desugaring | Since |
| --- | --- |
| `java.util.Optional`, `java.util.function`, `Stream` | API 24 |
| **`java.time`** (the reason 26 was chosen over 24) | API 26 |
| Adaptive icons, notification channels as the native path | API 26 |

Consequences to keep in mind:

- **Core library desugaring is deliberately not enabled.** No convention plugin sets
  `isCoreLibraryDesugaringEnabled`; `AndroidLibraryConventionPlugin` only sets
  Java 17 source/target compatibility. At API 26 the features that desugaring
  usually provides are already present, so the extra D8 step, the
  `coreLibraryDesugaring` dependency and its R8 interaction are all avoided.
- **Lowering `minSdk` again is one line** in `gradle/libs.versions.toml`, but below
  API 26 any `java.time` usage starts requiring desugaring, and below API 24 the
  same is true of `Optional`. This is why Dagger's `@BindsOptionalOf` was not used
  for optional dependencies: it requires `java.util.Optional`, which was unavailable
  at the old baseline. The template uses a possibly-empty `@Multibinds` set instead,
  which works at any API level and stays uniform with `Set<AppInitializer>`.

### Optionality is expressed with multibindings, not `Optional`

Optional collaborators are declared as a possibly-empty `Set<T>` via `@Multibinds`
and consumed as `Lazy<Set<T>>` where a dependency cycle has to stay broken (see
`TokenAuthenticator`). One mechanism covers both "zero or one" and "zero or many",
so the template teaches a single pattern rather than two.

### A module is only pluggable if `:app` never imports it

Dependency injection is the easy half of the plug-out contract. The half that
actually blocks deletion is the `import` line: Kotlin fails at compile time before
Dagger ever runs. Three collaborators — `IAnalyticsManager`, `NetworkMonitor` and
`LocaleManager` — had perfectly self-contained bindings and were still undeletable
purely because `MainActivity` and `AppNavigation` named their types.

The three fixes, one per shape of the problem:

| Problem shape | Fix | Applied to |
| --- | --- | --- |
| The type lives in the wrong module | Move it | `NetworkMonitor` → `core:common` |
| `:app` calls into an optional module | Invert it with an observer multibinding | analytics screen views → `NavigationObserver` |
| `:app` runs startup work for another module | Move it into an `AppInitializer` | locale restore → `LocaleInitializer` |

`MainActivity` now injects only `INavigationManager`, `ScreenRegistry`,
`NetworkMonitor` and `Set<NavigationObserver>`, all from modules that are never
deleted. The CI `plug-out` job is what keeps it that way.

### `core:network` is a transport layer, not a connectivity layer

`NetworkMonitor` was moved to `core:common` rather than solving the coupling by
making `core:network` a Compose module or by letting `core:ui` depend on
`core:network`. Both alternatives were rejected:

- Adding Compose to `core:network` would put UI concerns in a transport module to
  work around a layering mistake instead of fixing it.
- `core:ui` is a core module and `core:network` is optional. A core → optional edge
  breaks the plug-out contract outright.

The move costs nothing: `NetworkMonitor` imports only `android.net.*` and
`@ApplicationContext`, and `core:common` already applies the Hilt convention plugin.

### Extension points are added when there is a second user, not before

An injectable "app chrome" slot was considered so that optional modules could
contribute UI around the navigation host. It was dropped: once `NetworkStatus`
lives in `core:common`, the offline banner has no coupling left to solve, and the
mechanism would have shipped with zero users. `NavigationObserver` was kept because
analytics is a real, currently-optional consumer.

## Suggested remediation order

1. Make `scaffoldFeature` fail loudly when it cannot wire `settings.gradle.kts` or `app/build.gradle.kts` (item 1).
2. Persist the navigation back stack and make unresolved routes fail in debug builds (2, 3).
3. Add ViewModel tests for at least one full feature vertical and a `ScreenRegistry` coverage test (4, 3).
4. Pick one serialization stack; add a broad `catch` to `safeCall` (5, 6).
5. Move `isDarkModeFlow` to a theme/preferences contract (7).
6. Extract the CI secret bootstrap into a composite action (8).
7. Add a lint or detekt rule that fails the build when `:app` imports an optional module, so the plug-out contract is enforced at review time and not only by the CI delete job (9).

## Open questions for the maintainer

- Is the fixed 4-module feature shape intended to be non-negotiable, or should `scaffoldFeature` support a lighter UI-only variant?
- Should `ScreenRegistry` throw in debug builds and fall back only in release?
- Is Gson kept deliberately (backend contract flexibility) or is it legacy?
- Should `hardeningReport` and `scanApkForSecrets` be part of the CI release job rather than manual tasks?

---

[← Previous: 06 - Quality, Tests and CI](06-quality-tests-ci.md) · [Index](README.md) · [Next: 08 - Getting Started →](08-getting-started.md)
