# 05 - Generator and Scaffolding Tooling

## `scaffoldFeature`

Registered by `ScaffoldFeaturePlugin` in group `setup`.

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
./gradlew scaffoldFeature -PfeatureName=settings -PfeaturePkg=com.acme.app.feature
```

### Inputs and validation

| Property | Required | Default | Notes |
| --- | --- | --- | --- |
| `featureName` | Yes | — | Must be `lower_snake_case`, validated by regex |
| `featurePkg` | No | `com.ytapps.composetemplate.feature` | Base package for generated code |
| `withDatabase` | No | `false` | Adds Room entity + DAO to the `data` module |

The task fails if `feature/<name>/` already exists, so it is not a re-runnable updater.

### What gets generated

Always all four sub-modules, each with its own `build.gradle.kts` applying the matching `composetemplate.feature.*` plugin and namespace:

- **`domain`** — `Get<Name>TitleUseCase.kt`
- **`data`** — build script only; with `withDatabase=true` also `data/entity/<Name>Entity.kt` and `data/dao/<Name>Dao.kt` plus the `composetemplate.android.room` plugin
- **`navigation`** — `navigation/<Name>Route.kt`, with `kotlin.serialization` applied
- **`presentation`** — `<Name>UiState.kt`, `<Name>Event.kt`, `<Name>ViewModel.kt`, `<Name>Route.kt`, `<Name>ScreenProvider.kt`, `di/<Name>Module.kt`, and `res/values/strings.xml` + `res/values-tr/strings.xml`

Dependency wiring in the generated scripts: `data -> domain`, `presentation -> domain + navigation`.

The `feature.data` convention always provides `core:common` and `core:data`. It adds `core:network` and `core:database` only when those projects exist in the generated build. Deleting an unselected infrastructure module therefore does not leave a dangling convention-plugin dependency, while the four-module feature vertical remains unchanged.

### Build-file automation

None, by design. `settings.gradle.kts` discovers modules by scanning for directories that directly contain a `build.gradle.kts`, and `:app` derives its `core` and `feature` dependencies from the discovered projects. Writing the folders above is therefore all it takes to register the feature with the build; the task logs `no edit needed` for both files and continues to next steps and the suggested verification command `./gradlew :feature:<name>:presentation:compileDebugKotlin`.

This replaces an earlier mechanism worth knowing about, because its failure mode is a general lesson. The task used to append `include(":feature:<name>:<tier>")` lines to `settings.gradle.kts` and insert dependencies after the **last** line starting with `implementation(project(":feature:` in `app/build.gradle.kts`. The second edit was anchored on text: when the anchor was missing or reformatted, the task logged `not changed`, exited successfully, and left a feature that compiled on its own but was never part of the app. Deriving the list from the filesystem removes the anchor, and with it the failure.

## `create-new-app`

```bash
# Full architecture, including native secrets and runtime hardening
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain

# Same architecture without secrets or hardening infrastructure
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -PwithSecrets=false -q --console=plain
```

`withSecrets` is a strict Boolean and defaults to `true`. Feature shape does not change: every feature still has `data`, `domain`, `navigation` and `presentation`. The flag only controls project-level infrastructure.

The output is a consumer project, not a renamed copy of the template repository:

- Produces a **sibling** project directory (`../MyNewApp`).
- Rebrands package name, application name, convention-plugin prefix, namespaces, manifests and resources across the tree.
- Preserves the fixed four-module feature vertical and the `scaffoldFeature` task so development can continue with the same architecture.
- Replaces the template README and build-logic guide with short documentation for the generated application.
- Removes the generator implementation and registration, template wiki, MkDocs configuration, contribution guide, Pages publication workflow, and template-only CI jobs.
- Keeps consumer CI for lint, unit tests and debug/release assembly.
- Never carries `.git`, local build output, `local.properties`, `secrets.properties`, keystores or generated artifacts into the new project.
- With secrets selected, native bindings survive the rename because JNI methods are bound dynamically via `RegisterNatives` with the class path injected from the Gradle namespace (see [04](04-secrets-and-hardening.md)).

With `withSecrets=false`, generation removes the complete capability rather than asking the developer to clean it up later:

- `core/secrets` and the secrets-dependent `core/security` module
- NDK/CMake secret sources and the native-library convention plugin
- secret validation, artifact scanning and hardening-report tasks
- secret plugin registrations, root plugin application and Gradle property
- `secrets.properties.example`, secret setup instructions and CI bootstrap steps
- secret-dependent signing setup; generated release builds remain unsigned until the developer adds an application-specific signing strategy

Generation ends with a residue validation pass. It fails instead of reporting success if a forbidden template path remains or if searchable text still names the old package, generator plugin/task, template repository or published documentation site. For `withSecrets=false`, the same pass additionally rejects secret modules, native/validation plugins, tasks, properties, configuration keys and documentation references.

## Feature-tier convention plugins

`composetemplate.feature.domain|data|navigation|presentation` are the enforcement point for Clean Architecture: a generated module gets its allowed dependencies and toolchain from its tier plugin, so a feature author cannot casually pull UI libraries into `domain`.

## Flags observed in the build

| Flag | Effect |
| --- | --- |
| `withSecrets` | Selects secrets and hardening infrastructure during project generation; defaults to `true` |
| `composetemplate.useNativeSecrets` | Toggles the native path inside a generated project that selected secrets |
| `composetemplate.composeCompilerMetricsEnabled` | Compose compiler metrics output |
| `composetemplate.composeCompilerReportsEnabled` | Compose compiler reports output |

---

[← Previous: 04 - Secrets, Security and Hardening](04-secrets-and-hardening.md) · [Index](README.md) · [Next: 06 - Quality, Tests and CI →](06-quality-tests-ci.md)
