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

### Build-file automation

1. **`settings.gradle.kts`** — appends four `include(":feature:<name>:<tier>")` lines, guarded by a `contains(":feature:<name>:")` check.
2. **`app/build.gradle.kts`** — finds the **last** line starting with `implementation(project(":feature:` and inserts the four new dependencies after it.

The task then logs whether each file was actually updated, plus next steps and the suggested verification command `./gradlew :feature:<name>:presentation:compileDebugKotlin`.

> **Warning:** Both edits are line/text based. If the anchor line in `app/build.gradle.kts` is absent or reformatted, the task reports `not changed` and continues successfully — leaving a generated feature that compiles standalone but is never included in the app. Always read the task output.

## `create-new-app`

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

- Produces a **sibling** project directory (`../MyNewApp`).
- Rebrands package name, application name, namespaces, manifests and resources across the tree.
- Native bindings survive the rename because JNI methods are bound dynamically via `RegisterNatives` with the class path injected from the Gradle namespace (see [04](04-secrets-and-hardening.md)).
- CI asserts the generated project contains **no** `secrets.properties`, no `local.properties`, and no `.git` directory — i.e. no leaked credentials and no inherited history.

## Feature-tier convention plugins

`composetemplate.feature.domain|data|navigation|presentation` are the enforcement point for Clean Architecture: a generated module gets its allowed dependencies and toolchain from its tier plugin, so a feature author cannot casually pull UI libraries into `domain`.

## Flags observed in the build

| Flag | Effect |
| --- | --- |
| `composetemplate.useNativeSecrets` | Toggles native secret pipeline (defaults on) |
| `composetemplate.composeCompilerMetricsEnabled` | Compose compiler metrics output |
| `composetemplate.composeCompilerReportsEnabled` | Compose compiler reports output |

---

[← Previous: 04 - Secrets, Security and Hardening](04-secrets-and-hardening.md) · [Index](README.md) · [Next: 06 - Quality, Tests and CI →](06-quality-tests-ci.md)
