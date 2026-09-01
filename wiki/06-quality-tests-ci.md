# 06 - Quality, Tests and CI

## Static analysis

Centralized in a static-analysis convention plugin: **ktlint** (plugin 14.2.0) and **detekt** 1.23.8, with `.editorconfig` at the root. Entry points: `./gradlew ktlintCheck detekt`.

### Module boundary enforcement

`composetemplate.app.boundary` is applied by the application convention plugin, so only application modules get it. It registers `checkAppModuleBoundary`, hooked into both `preBuild` and `check`, which parses every `import` under `app/src` and fails the build when the application module names a symbol from a module that is meant to be removable.

The rule is an **allowlist, not a blocklist**: `:app` may import `core.common`, `core.navigation`, `core.ui` and its own packages. Anything else under `core.*` or `feature.*` is a violation. A module added later is therefore forbidden by default, and nobody has to remember to extend a list.

Why a dedicated task instead of detekt's `ForbiddenImport`: the detekt configuration is a single root file shared by every module (`config.setFrom(files("$rootDir/config/detekt/detekt.yml"))`), so forbidding `core.analytics` imports globally would also flag `core:analytics`'s own sources. The boundary is a property of one module, so it is checked where it applies.

The failure message names the file, the line, the offending import and the mechanism to use instead — an `AppInitializer` for startup work, a `NavigationObserver` for navigation events, or moving a misplaced type into `core:common`.

Relationship to the CI `plug-out` job: the job proves that the modules it deletes are actually removable, after the fact and only for the modules listed in `ci.yml`. The Gradle task prevents the regression up front, for every module, on every build.

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

- `:baselineprofile` module with the `androidx.baselineprofile` plugin, consumed by `app` via `baselineProfile(project(":baselineprofile"))`.
- `:benchmark` module with Macrobenchmark + UI Automator.
- Dedicated `benchmark` build type in `app` (`initWith(release)` + `benchmark-rules.pro`) and `androidx.profileinstaller` at runtime.
- Neither benchmarks nor baseline-profile generation run in CI — the infrastructure exists, the measurement loop does not.

## CI: `.github/workflows/ci.yml`

JDK 17 (temurin), `gradle/actions/setup-gradle@v4`, concurrency group with `cancel-in-progress`. Five jobs:

1. **lint** — `ktlintCheck` + `detekt`
2. **test** — `testDebugUnitTest`
3. **build** — `assembleDebug` + `:app:assembleRelease`
4. **plug-out** — deletes the optional modules listed in the job (`core/security`, `core/analytics`), strips their `include(...)` and `project(...)` lines, greps the Kotlin sources to prove nothing still references them, then runs `:app:assembleDebug`. The list grows with each plug-out refactor, which is what makes a regression unmergeable.
5. **template-smoke** — the distinctive one:
   - `help --task scaffoldFeature`
   - `scaffoldFeature -PfeatureName=ci_feature`, then compile it
   - `scaffoldFeature -PfeatureName=ci_database_feature -PwithDatabase=true`, then compile `data` + `presentation`
   - `create-new-app -Pargs='com.example.generated,GeneratedApp'`
   - assert the generated project has no `secrets.properties`, no `local.properties`, no `.git`

The smoke job is the mechanism that keeps the generator honest: if a template file drifts and no longer compiles after generation, CI fails even though the template repository itself still builds.

### CI weak points

- Every job recreates `local.properties` and `secrets.properties` with an inline heredoc — the same block is duplicated **five times**, so any secret-key change must be applied in five places.
- No instrumentation tests, no benchmark run.
- No report/coverage artifact upload; failures must be read from logs.
- A sample signature hash is embedded in the workflow for release assembly.
- The workflow file is YAML, so an invisible character on a blank line inside a `run: |` block makes GitHub skip the workflow entirely and report **zero** check runs rather than a failure. A PR that looks unchecked is the symptom.

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
