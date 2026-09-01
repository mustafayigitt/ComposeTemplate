# 07 - Risks, Gaps and Open Questions

Findings from reading the code, ordered by practical impact. Each item is an observation with a concrete location, not a style opinion.

## High impact

| # | Finding | Where | Why it matters |
| --- | --- | --- | --- |
| 1 | Navigation back stack is not persisted | `NavigationManager` | A `@Singleton` in-memory `MutableStateFlow<List<INavigationItem>>` is lost on process death; users return to the start destination with no state restoration |
| 2 | Unresolved routes render text instead of failing | `ScreenRegistry` | Fallback shows `"Screen not found: <route>"`; a DI wiring mistake ships as a broken screen rather than a build/test failure |
| 3 | Test pyramid inverted | whole repo | 5 unit tests, all infrastructure; 8 presentation modules and the `BaseViewModel` contract are untested |

## Medium impact

| # | Finding | Where | Why it matters |
| --- | --- | --- | --- |
| 4 | Two serialization stacks | `NetworkModule` (Gson) vs `kotlin.serialization` for routes/models | Extra dependency surface, reflection, and ProGuard-keep burden for no functional gain |
| 5 | `safeCall` catches only `HttpException` and `IOException` | `BaseRepository` | Deserialization or `IllegalState` failures escape the `Result` abstraction and can crash callers that assume total coverage |
| 6 | Theme state exposed through the navigation contract | `INavigationManager.isDarkModeFlow` | Misplaced responsibility; consumers depend on navigation to read appearance settings |
| 7 | CI secret bootstrap duplicated five times | `.github/workflows/ci.yml` | Drift risk; a reusable workflow or composite action would collapse it into one definition |
| 8 | Module-graph enforcement stops at `:app` | build-logic | `checkAppModuleBoundary` now fails the build when the application module imports a pluggable module, but every other edge is still guarded by review alone: a feature may import another feature, and a core module may import an optional one |
| 9 | `SecretManager` is a global object requiring `initialize(context)` | `core:secrets` | Initialization-order coupling; a secret read before startup completes fails at runtime rather than compile time |

## Lower impact / by design but worth stating

| # | Finding | Note |
| --- | --- | --- |
| 10 | Bottom-bar order derives from multibinding key strings | Ordering is a naming convention, not a typed contract |
| 11 | Fixed 4-module shape for every feature | Consistent, but trivial features (`splash`, `detail`) pay full configuration cost — 32 modules for 8 features |
| 12 | Unbuffered event `Channel` in `BaseViewModel` | Single-consumer, rendezvous semantics; needs to be documented for feature authors |
| 13 | `runBlocking` inside `TokenAuthenticator` | Required by OkHttp's blocking `Authenticator` API; rationale is in the source |
| 14 | Certificate pinning disabled in debug | Pin misconfiguration only appears in release builds |
| 15 | `config` module is local-only | `IConfigManager` + `LocalConfigProvider`; no remote-config backend, so runtime flags require a release |
| 16 | Benchmarks and baseline profiles never run in CI | Performance infrastructure exists but is unmeasured |

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

### Modules are discovered, not declared

`settings.gradle.kts` scans the tree and includes every directory that directly
contains a `build.gradle.kts`; `:app` builds its `core` and `feature` dependency
list from the projects that scan produced. Neither file names a module.

The reason is the plug-out contract's fourth criterion: deleting a module's folder
must leave a working build. That was never actually true while the module was also
named in two other files. A dangling `include()` does not break one module, it
breaks configuration for the whole build, so "delete the folder" was really a
three-file edit that CI had to reproduce with `sed`.

The same change removes the mirror-image problem in `scaffoldFeature`, which used to
patch both files after generating a feature — see [05](05-generator-and-scaffolding.md).

Two details are load-bearing:

- **`build-logic` must stay out of the scan.** It is an included build, not a
  project; including it as one breaks the build. It sits in the skip list next to
  `build`, `buildSrc`, `gradle` and `src`.
- **Directories are traversed even after being included.** Core modules are one
  level deep and feature modules are two, so recursion cannot stop at the first
  match. Intermediate paths such as `feature/auth` own no build file, are never
  included as projects, and are filtered out of `:app`'s dependency list by a
  `buildFile.isFile` check.

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
deleted.

### The plug-out contract is a build rule, not a review convention

Keeping `:app` clean by review does not survive contact with a growing template, so
the rule is executable. `composetemplate.app.boundary` registers
`checkAppModuleBoundary`, wired into `preBuild` and `check`, which reads every
`import` under `app/src` and fails when the application module names a symbol from a
pluggable module.

Three properties were deliberate:

- **Allowlist, not blocklist.** The check permits `core.common`, `core.navigation`,
  `core.ui` and the app's own packages, and rejects everything else under `core.*`
  or `feature.*`. A blocklist would need an edit every time a module is added, and
  the edit that gets forgotten is exactly the one that matters. This matters more now
  that modules appear in the build merely by existing on disk.
- **Anchored on the application namespace.** Imports are compared against the
  module's own package root, read from the `namespace` through a lazy provider.
  Matching a bare `.core.` substring would flag `androidx.core.view.WindowCompat`.
- **A Gradle task, not a detekt rule.** `config/detekt/detekt.yml` is one root file
  shared by every module, so a global forbidden-import entry for `core.analytics`
  would also fail `core:analytics`'s own sources. The boundary belongs to a single
  module and is enforced there.

This complements rather than replaces the CI `plug-out` job. The job proves that the
modules it deletes are genuinely removable, after the fact and only for the modules
listed in `ci.yml`; the task blocks the regression before it is committed, for every
module.

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

1. Persist the navigation back stack and make unresolved routes fail in debug builds (1, 2).
2. Add ViewModel tests for at least one full feature vertical and a `ScreenRegistry` coverage test (3, 2).
3. Pick one serialization stack; add a broad `catch` to `safeCall` (4, 5).
4. Move `isDarkModeFlow` to a theme/preferences contract (6).
5. Extract the CI secret bootstrap into a composite action (7).
6. Extend module-graph enforcement beyond the application module (8). `checkAppModuleBoundary` covers `:app`; still unenforced are feature → feature imports and core → optional edges. Both need a rule that can express per-module allowlists without a shared global configuration.

## Open questions for the maintainer

- Is the fixed 4-module feature shape intended to be non-negotiable, or should `scaffoldFeature` support a lighter UI-only variant?
- Should `ScreenRegistry` throw in debug builds and fall back only in release?
- Is Gson kept deliberately (backend contract flexibility) or is it legacy?
- Should `hardeningReport` and `scanApkForSecrets` be part of the CI release job rather than manual tasks?

---

[← Previous: 06 - Quality, Tests and CI](06-quality-tests-ci.md) · [Index](README.md) · [Next: 08 - Getting Started →](08-getting-started.md)
