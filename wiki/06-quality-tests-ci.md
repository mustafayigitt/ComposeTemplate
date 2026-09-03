# 06 - Quality, Tests and CI

## Static analysis

Centralized in a static-analysis convention plugin: **ktlint** (plugin 14.2.0) and **detekt** 1.23.8, with `.editorconfig` at the root. Entry points: `./gradlew ktlintCheck detekt`.

### Module boundary enforcement

`composetemplate.app.boundary` is applied by the application convention plugin, so only application modules get it. It registers `checkAppModuleBoundary`, hooked into both `preBuild` and `check`, which parses every `import` under `app/src` and fails the build when the application module names a symbol from a module that is meant to be removable.

The rule is an **allowlist, not a blocklist**: `:app` may import `core.common`, `core.navigation`, `core.ui` and its own packages. Anything else under `core.*` or `feature.*` is a violation. A module added later is therefore forbidden by default, and nobody has to remember to extend a list.

Why a dedicated task instead of detekt's `ForbiddenImport`: the detekt configuration is a single root file shared by every module (`config.setFrom(files("$rootDir/config/detekt/detekt.yml"))`), so forbidding `core.analytics` imports globally would also flag `core:analytics`'s own sources. The boundary is a property of one module, so it is checked where it applies.

The failure message names the file, the line, the offending import and the mechanism to use instead — an `AppInitializer` for startup work, a `NavigationObserver` for navigation events, or moving a misplaced type into `core:common`.

Relationship to the CI `plug-out` job: the job proves that the modules it deletes are actually removable, after the fact and only for the modules listed in `ci.yml`. The Gradle task prevents the regression up front, for every module, on every build.

What the task cannot see is a build-file coupling. It reads `import` lines, so a module named only in `app/build.gradle.kts` — which is exactly how the performance modules used to be wired — passes the check and still blocks deletion. That shape is caught by the `plug-out` job instead.

## Test stack

Standardized by `composetemplate.test`: JUnit 4.13.2, MockK 1.14.11, Truth 1.4.5, `kotlinx-coroutines-test` 1.11.0, plus AndroidX JUnit/Espresso and Compose UI test artifacts declared in the catalog.

### Actual unit tests in the repository

| Test | Module | Covers |
| --- | --- | --- |
| `NavigationManagerTest` | `core:navigation` | Back-stack operations, tab selection |
| `BaseRepositoryTest` | `core:network` | `safeCall` status-code mapping |
| `TokenAuthenticatorTest` | `core:network` | Refresh, retry cap, concurrency guard |
| `AuthRepositoryTest` | `feature:auth:data` | Repository happy/error paths |
| `LocaleManagerTest` | `core:data` | Language persistence and restore |

> **Note:** The test pyramid is inverted: the five tests all target infrastructure. None of the 8 presentation modules has a ViewModel or Compose UI test, so the `BaseViewModel` state/event contract and screen registration are not covered by CI.

## Performance infrastructure

- `:baselineprofile` module, configured by `composetemplate.baseline.profile.generator`.
- `:benchmark` module with Macrobenchmark + UI Automator. It does **not** use that convention plugin: it applies `com.android.test` directly and repeats the same configuration by hand.
- Dedicated `benchmark` build type in `app` (`initWith(release)` + `benchmark-rules.pro`). This one stays in the build file on purpose — it names no module, and the performance modules select it through `matchingFallbacks` rather than the reverse.
- `composetemplate.perf` owns the rest of the wiring: applying `androidx.baselineprofile`, the `baselineProfile(project(":baselineprofile"))` dependency and `androidx.profileinstaller` at runtime. All three are contributed **only when `:baselineprofile` is present in the build**, so deleting the performance folders leaves a working application module. See the decision log in [07](07-risks-and-gaps.md#baseline-decision-log).
- Neither benchmarks nor baseline-profile generation run in CI — the infrastructure exists, the measurement loop does not. What CI does verify is that the infrastructure is removable.

## CI: `.github/workflows/ci.yml`

JDK 17 (temurin), `gradle/actions/setup-gradle@v4`, concurrency group with `cancel-in-progress`. Five jobs:

1. **lint** — `ktlintCheck` + `detekt`. Also the job that compiles `build-logic`, so convention-plugin errors surface here first.
2. **test** — `testDebugUnitTest`
3. **build** — `assembleDebug` + `:app:assembleRelease`
4. **plug-out** — the contract's executable half. It deletes `core/security`, `core/analytics`, `benchmark` and `baselineprofile`, greps the Kotlin sources to prove nothing still references the two `core` modules, then runs `:app:assembleDebug`. The list grows with each plug-out refactor, which is what makes a regression unmergeable.
5. **template-smoke** — the distinctive one:
   - `help --task scaffoldFeature`
   - `scaffoldFeature -PfeatureName=ci_feature`, then compile it
   - `scaffoldFeature -PfeatureName=ci_database_feature -PwithDatabase=true`, then compile `data` + `presentation`
   - `create-new-app -Pargs='com.example.generated,GeneratedApp'`
   - assert the generated project has no `secrets.properties`, no `local.properties`, no `.git`

The smoke job is the mechanism that keeps the generator honest: if a template file drifts and no longer compiles after generation, CI fails even though the template repository itself still builds.

### Two details of the plug-out job worth knowing

**Deleting the folder is the entire operation.** The job used to run two `sed` commands per module to strip `include(...)` from `settings.gradle.kts` and `project(...)` from `app/build.gradle.kts`. Once modules were discovered from disk those commands matched nothing, and they have been removed. If they are ever reintroduced, they are a sign that a module has been hardcoded somewhere again.

**A green build is not sufficient proof.** `:app:assembleDebug` succeeding after the deletion only shows that nothing exploded; it does not show that `composetemplate.perf` took its conditional path. The job therefore asserts the plugin's log line:

```bash
./gradlew :app:assembleDebug | tee plug-out-build.log
grep -q "Baseline profiles are disabled" plug-out-build.log
```

Without that assertion the job would still pass if the plugin silently stopped being applied, or started wiring baseline profiles unconditionally again. Note the `set -euo pipefail`: piping Gradle into `tee` would otherwise hide a Gradle failure behind `tee`'s exit code.

### CI weak points

- Every job recreates `local.properties` and `secrets.properties` with an inline block — the same 14 lines are duplicated **five times**, so any secret-key change must be applied in five places.
- No instrumentation tests. Benchmarks are proven removable but never actually run, so no performance number is ever measured.
- No report/coverage artifact upload; failures must be read from logs.
- A sample signature hash is embedded in the workflow for release assembly.
- The workflow file is YAML, so an invisible character on a blank line inside a `run: |` block makes GitHub skip the workflow entirely and report **zero** check runs rather than a failure. A PR that looks unchecked is the symptom. To clear it:

```bash
perl -CSD -pi -e 's/\x{200b}//g' .github/workflows/ci.yml
```

## Local verification equivalent

```bash
./gradlew validateSecrets
./gradlew checkAppModuleBoundary
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
./gradlew scanApkForSecrets hardeningReport
```

`checkAppModuleBoundary` also runs as part of `preBuild`, so any `assemble*` task already covers it; the explicit invocation is for a fast boundary-only check.

---

[← Previous: 05 - Generator and Scaffolding Tooling](05-generator-and-scaffolding.md) · [Index](README.md) · [Next: 07 - Risks, Gaps and Open Questions →](07-risks-and-gaps.md)
