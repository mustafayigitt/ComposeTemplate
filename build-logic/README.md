# Build Logic

`build-logic` is an included Gradle build that owns ComposeTemplate's generation, architecture, quality, optional-infrastructure, and verification conventions.

## Registered convention plugins (21)

| Area | Plugin IDs |
| --- | --- |
| Application/library | `composetemplate.android.application`, `android.application.compose`, `android.library`, `android.library.compose` |
| Android capabilities | `android.hilt`, `android.room`, `android.library.native` |
| Feature layers | `feature.domain`, `feature.data`, `feature.navigation`, `feature.presentation` |
| Quality/boundaries | `test`, `static.analysis`, `app.boundary`, `module.boundary` |
| Generation | `create.new.app`, `data.strategy.projection`, `scaffold.feature` |
| Operations | `validate.secrets`, `baseline.profile.generator`, `perf` |

The exact registrations live in `convention/build.gradle.kts`.

## Generation

### `composetemplate.create.new.app`

Copies the source template into a sibling project, rebrands package/application names, excludes local/template-only files, writes consumer documentation, and validates that template-only residue is absent.

### `composetemplate.data.strategy.projection`

Decorates `create-new-app` with strict `remote`, `offline-first`, `local`, and `minimal` projection. It selects network/auth and Room infrastructure, projects the correct Login/Home navigation flow, applies effective secrets selection, removes unselected catalog/build/source/documentation residue, and deletes generator-only code from the consumer.

### `composetemplate.scaffold.feature`

Creates the fixed `data`, `domain`, `navigation`, and `presentation` feature vertical. In database-backed consumers, `-PwithDatabase=true` adds starter Room entity/DAO files. Filesystem module discovery means scaffolding does not edit settings or app build files.

## Architecture enforcement

- `app.boundary` prevents `:app` source imports from optional modules.
- `module.boundary` registers source-import and literal project-dependency checks for library modules.
- Dynamic project paths remain supported for filesystem discovery.
- Per-module exceptions are explicit in that module's `moduleBoundary` block.

## Optional infrastructure

- `feature.data` wires only infrastructure selected in the generated consumer.
- `perf` enables baseline-profile wiring only when `:baselineprofile` exists.
- `android.room` owns Room/KSP configuration.
- `android.library.native` owns CMake/NDK configuration.
- `validate.secrets` provides `validateSecrets`, `scanApkForSecrets`, and `hardeningReport`.

## Quality and shared configuration

Android, Compose, Hilt, testing, static analysis, SDK values, and dependencies are centralized in convention plugins and `gradle/libs.versions.toml`. Current baseline: minSdk 26, targetSdk 36, compileSdk 37, Kotlin 2.0.21, AGP 9.2.1, and KSP 2.0.21-1.0.28.

Compose compiler metrics and reports are controlled by:

```properties
composetemplate.composeCompilerMetricsEnabled=true
composetemplate.composeCompilerReportsEnabled=true
```

See [`../wiki/01-module-topology.md`](../wiki/01-module-topology.md), [`../wiki/05-generator-and-scaffolding.md`](../wiki/05-generator-and-scaffolding.md), and [`../wiki/06-quality-tests-ci.md`](../wiki/06-quality-tests-ci.md).
