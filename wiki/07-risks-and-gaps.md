# 07 - Risks, Gaps and Open Questions

Findings from reading the code, ordered by practical impact. Each item is an observation with a concrete location, not a style opinion.

## High impact

| # | Finding | Where | Why it matters |
| --- | --- | --- | --- |
| 1 | Navigation back stack is not persisted | `NavigationManager` | A `@Singleton` in-memory `MutableStateFlow<List<INavigationItem>>` is lost on process death; users return to the start destination with no state restoration |
| 2 | Unresolved routes render text instead of failing | `ScreenRegistry` | Fallback shows `"Screen not found: <route>"`; a DI wiring mistake ships as a broken screen rather than a build/test failure |
| 3 | Test pyramid inverted | whole repo | The only unit tests are `BaseRepositoryTest` and `TokenAuthenticatorTest`, both `core:network` infrastructure; 8 presentation modules and the `BaseViewModel` contract are untested |

## Medium impact

| # | Finding | Where | Why it matters |
| --- | --- | --- | --- |
| 4 | Two serialization stacks | `NetworkModule` (Gson) vs `kotlin.serialization` for routes/models | Extra dependency surface, reflection, and ProGuard-keep burden for no functional gain |
| 5 | `safeCall` catches only `HttpException` and `IOException` | `BaseRepository` | Deserialization or `IllegalState` failures escape the `Result` abstraction and can crash callers that assume total coverage |
| 6 | Theme state exposed through the navigation contract | `INavigationManager.isDarkModeFlow` | Misplaced responsibility; consumers depend on navigation to read appearance settings |
| 7 | CI secret bootstrap duplicated five times | `.github/workflows/ci.yml` | Drift risk; a reusable workflow or composite action would collapse it into one definition |
| 8 | Boundary enforcement sees imports, not build files | build-logic | Import-level enforcement now covers every module (see the decision log), but the check reads Kotlin `import` lines only. A `project(":…")` line in a build script couples two modules without an import — the exact shape that once blocked deleting `:baselineprofile` |
| 9 | `SecretManager` is a global object requiring `initialize(context)` | `core:secrets` | Initialization-order coupling; a secret read before startup completes fails at runtime rather than compile time |

## Lower impact / by design but worth stating

| # | Finding | Note |
| --- | --- | --- |
| 10 | Bottom-bar order derives from multibinding key strings | Ordering is a naming convention, not a typed contract |
| 11 | Fixed 4-module shape for every feature | Consistent, but trivial features (`splash`, `detail`) pay full configuration cost — 32 modules for 8 features. `feature/home/domain` currently contains only a `build.gradle.kts` and no sources at all |
| 12 | Unbuffered event `Channel` in `BaseViewModel` | Single-consumer, rendezvous semantics; needs to be documented for feature authors |
| 13 | `runBlocking` inside `TokenAuthenticator` | Required by OkHttp's blocking `Authenticator` API; rationale is in the source |
| 14 | Certificate pinning disabled in debug | Pin misconfiguration only appears in release builds |
| 15 | `config` module is local-only | `IConfigManager` + `LocalConfigProvider`; no remote-config backend, so runtime flags require a release |
| 16 | Benchmarks and baseline profiles never run in CI | Performance infrastructure exists but is unmeasured. CI proves the tooling is *removable*; it never proves a benchmark *runs*. See the decision log below |
| 17 | `benchmark/build.gradle.kts` bypasses its own convention plugin | It applies `com.android.test` directly and repeats every setting that `composetemplate.baseline.profile.generator` already applies for `:baselineprofile` |
| 18 | The CI plug-out job deletes 4 of roughly 12 optional modules | It removes `core/security`, `core/analytics`, `benchmark` and `baselineprofile`. `core:network`, `core:database`, `core:google-play`, `core:permission`, `core:config`, `core:secrets` and the 8 features are *argued* to be removable, not demonstrated. The boundary check closes the import half of that gap for every module, but no job actually deletes those folders and rebuilds |
| 19 | `mkdocs build --strict` is not a pull-request check | The published site builds from `main` after merge, so a broken link or nav entry fails once it is already public rather than in review |
| 20 | `main` is unprotected | Nothing requires the five checks to pass before merging, and a fast merge can outrun CI entirely: PR #25 was merged about a minute after it was opened, so none of its checks ever ran |

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

### Performance tooling is wired conditionally, not unconditionally

Discovery made module folders deletable, but `:app` still reached out to the
performance modules by name. Three lines in `app/build.gradle.kts` did it:
`alias(libs.plugins.baselineprofile)`, `baselineProfile(project(":baselineprofile"))`
and the `profileinstaller` runtime dependency. The first two fail configuration of
the application module the moment `baselineprofile/` is deleted, which made the
performance layer the last remaining violation of the contract's third criterion —
a module's Gradle wiring must be self-contained.

`composetemplate.perf` now owns all three. It calls
`rootProject.findProject(":baselineprofile")` and does nothing but log when the
result is `null`. Because modules are discovered from disk, that lookup and the
question "does the folder still exist" are the same question, so there is no flag to
keep in sync. `findProject` is used rather than `project(path)` specifically because
the latter throws when the project is absent, which is the failure being removed.

Two consequences worth knowing before touching this:

- **The plugin is applied by id, and that requires a line in the root build script.**
  `androidx.baselineprofile` reached the plugin classpath only through `:app`'s
  `plugins {}` block, so removing that alias would have made
  `pluginManager.apply("androidx.baselineprofile")` fail with "plugin not found". It
  is now declared `apply false` at the root, the same mechanism
  `AndroidHiltConventionPlugin` already relies on for Hilt. Applying by id also means
  the convention project needs no `compileOnly` dependency, since no typed extension
  is configured.
- **The `benchmark` build type stays in `app/build.gradle.kts` on purpose.** It looks
  like performance wiring, but it references only app-local files
  (`benchmark-rules.pro` and the release signing config) and names no module. The
  performance modules select it through `matchingFallbacks`, not the other way
  around, so it remains valid after they are deleted. Moving it into the plugin would
  mean calling `getDefaultProguardFile(...)` through
  `com.android.build.api.dsl.ApplicationExtension`, which is not verified to expose
  it; an unnecessary risk for no gain in removability.

Still open: `benchmark/build.gradle.kts` does not use
`composetemplate.baseline.profile.generator` at all (finding 17). Folding it in needs
`targetSdk`, `testInstrumentationRunner`, the `androidx.benchmark.suppressErrors`
argument and a build type added to that plugin, none of which are confirmed against
`TestExtension` yet, so it was deliberately left out of this change.

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
listed in `ci.yml`; the task blocks the regression before it is committed.

### The same rule now applies to every module, and it was proven by making it fail

`checkAppModuleBoundary` guarded one module. Every other edge in the graph was still
guarded by review alone, and two of those edges break the plug-out contract just as
thoroughly:

- a feature importing another feature's `data` or `presentation` code makes the two
  features deletable only as a pair;
- a core module that survives every plug-out combination importing an optional one
  makes that optional module undeletable **everywhere**, because the importing module
  is never the one being deleted.

`composetemplate.module.boundary`, applied by `composetemplate.android.library`,
registers `checkModuleBoundary` for all 44 library modules — every `core:*` and every
`feature:*:*`, including `feature:*:domain`, which is an Android library rather than a
JVM module. No build script opts in. The rule is derived from the module's own Gradle
path, so a module added later is governed the moment its folder exists on disk, which
is the same property that made module discovery worth doing.

| Module | May not name | Permitted anyway |
| --- | --- | --- |
| `:app` | `core.*`, `feature.*` | `core.common`, `core.navigation`, `core.ui` |
| `core:common`, `core:navigation`, `core:ui`, `core:data` | `core.*`, `feature.*` | those four, plus itself |
| any other `core:*` | `feature.*` | any core module, never a feature |
| `feature:X:*` | `feature.*` | `feature.X.*`, and any `feature.*.navigation.*` |

**The navigation exception is design, not a loophole added to keep the build green.**
`feature/auth/presentation` already declares `implementation(project(":feature:splash:navigation"))`
to link to a route it does not own. A rule banning every cross-feature import would
have declared that intended edge a violation on day one. So navigation is modelled as
what a feature *publishes*: its route contract is public, its data and presentation
code are private, and coupling to the latter is what actually stops two features from
being removed independently. Permitted patterns therefore support `*` as a single
package segment, which lets `feature.*.navigation.` be written once instead of once
per feature.

A module needing a narrow exception declares it in its own build script rather than in
a shared file, for the same reason the check is a Gradle task and not a detekt rule:

```kotlin
moduleBoundary {
    additionalPermittedImports.add("feature.auth.domain.")
}
```

**How it was proven.** A green pipeline cannot establish that a check works, because a
task that has quietly become inert produces exactly the same green. So the check was
deliberately broken, once per rule branch, with probe commits that were then reverted:

| Run | What it establishes |
| --- | --- |
| `33846040803` | Clean code, 5/5 green — the rules raise no false alarms on the graph as it stands |
| `33847159068` | Control case: the task graph collapsed, `4 actionable tasks`, **no check ran at all** |
| `33847887311` | Core rule rejects a violation: `:core:data:checkModuleBoundary FAILED`, `19 actionable tasks: 17 executed` |
| `33848373441` | Feature rule rejects a violation: red only on the jobs that build `:app`, while Lint and Template Smoke stay green |
| `33849020026` | After the revert, 5/5 green again on a tree identical to the pre-probe commit |

The control case is the load-bearing row, and it is why the count of actionable tasks
is quoted. The first probe pointed `core:data` at `core:network` — but `core:network`
already depends on `core:data`, so the reverse edge closed a loop, Gradle rejected the
task graph before running anything, and four red jobs proved nothing whatsoever. The
outer signature was identical to a real violation. `4 actionable tasks` against
`17 executed` is what separates "the check ran and rejected the import" from "the build
died first".

Two lessons worth keeping for the next probe:

- **Adding a reverse edge into an always-present core module tends to create a cycle,**
  because the optional modules already depend on the always-present ones. Read the
  target module's `build.gradle.kts` before authoring the violation. The retargeted
  probe used `core:config`, which depends only on `:core:common`.
- **Enumerate the failure modes that precede the mechanism under test,** not only the
  ones that follow it. The probe was built so the boundary check was the only thing
  that *could* fail — the violating import resolved to a real public symbol, the
  project dependency was declared, and the import was used, so neither an unresolved
  reference nor a ktlint unused-import could be mistaken for the rule firing.
  `checkModuleBoundary` runs from `preBuild`, before `compileKotlin`, which is also
  why `ktlintCheck` stays green during a violation: it never triggers `preBuild`.

One implementation detail is easy to "tidy" back into a bug. The failure report travels
in the `GradleException` message rather than through `logger.error`. Gradle flushes
console output line by line and attributes each line to whichever task is current, so
under parallel execution a multi-line error came apart — the header printed beneath a
different module's task, with the offending import far below it. For a check whose
whole purpose is to name the module that broke the rule, a report separable from its own
module name is a defect. An exception message is printed under `* What went wrong:` as
one block, already prefixed with the failing task path.

What this still does not catch is build-file coupling (finding 8): the performance
wiring was a `project(":baselineprofile")` line, not an `import`. Boundary violations
come in both shapes, and only one of them is enforced today.

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

Worth knowing when reading the graph: `core:network` itself depends on `core:data`.
That is the permitted direction — optional depending on always-present — but it shows
how central `core:data` is, which is why `core:data` is one of the four modules held to
the strictest rule above.

### Extension points are added when there is a second user, not before

An injectable "app chrome" slot was considered so that optional modules could
contribute UI around the navigation host. It was dropped: once `NetworkStatus`
lives in `core:common`, the offline banner has no coupling left to solve, and the
mechanism would have shipped with zero users. `NavigationObserver` was kept because
analytics is a real, currently-optional consumer.

## Suggested remediation order

1. Persist the navigation back stack and make unresolved routes fail in debug builds (1, 2).
2. Add ViewModel tests for at least one full feature vertical and a `ScreenRegistry` coverage test (3, 2).
3. Pick one serialization stack; add a broad `catch` to `safeCall` (4, 5). A broad catch must rethrow `CancellationException` before mapping anything else, or a cancelled coroutine is silently converted into a failed `Result` and the caller treats teardown as an error.
4. Move `isDarkModeFlow` to a theme/preferences contract (6).
5. Extract the CI secret bootstrap into a composite action (7).
6. Extend boundary enforcement to build files (8). The import half shipped: `checkModuleBoundary` covers every module, with per-module rules and a per-module escape hatch. The remaining half is a check that scans module `build.gradle.kts` files for `project(":…")` edges against the same allowlists — an import scanner cannot see that shape by construction. Such a check has to reason about dependency direction rather than pattern-match, since a reverse edge into an always-present module is also how a task-graph cycle gets created.
7. Fold `benchmark/build.gradle.kts` into `composetemplate.baseline.profile.generator` so the two performance modules stop configuring themselves differently (17).
8. Give the plug-out job broader coverage, or state its limits in the README (18). Four modules are proven removable; the rest are argued.

## Open questions for the maintainer

- Is the fixed 4-module feature shape intended to be non-negotiable, or should `scaffoldFeature` support a lighter UI-only variant? (`feature/home/domain` is currently an empty module, which is the same question asked by the tree.)
- Should `ScreenRegistry` throw in debug builds and fall back only in release?
- Is Gson kept deliberately (backend contract flexibility) or is it legacy?
- Should `hardeningReport` and `scanApkForSecrets` be part of the CI release job rather than manual tasks?

---

[← Previous: 06 - Quality, Tests and CI](06-quality-tests-ci.md) · [Index](README.md) · [Next: 08 - Getting Started →](08-getting-started.md)
