# CI Pipeline

ComposeTemplate's CI validates both the current template app and the generator behavior.

That distinction is important: a template repository is not healthy just because the current app builds. It must also prove that generated output works.

## Problem

Normal Android CI often checks only the app currently in the repository:

- lint,
- unit tests,
- debug build,
- release build.

For a template generator, that is not enough. Generator regressions can break newly created apps even when the template app still compiles.

ComposeTemplate's CI includes a template smoke test to catch this.

## Trigger policy

CI runs on:

- pull requests to `main` and `develop`,
- pushes to `main`.

Concurrency cancels in-progress runs for the same workflow/ref so outdated runs do not waste time.

## Jobs

### Lint

Runs:

```bash
./gradlew ktlintCheck
./gradlew detekt
```

This catches formatting and static-analysis issues.

### Unit tests

Runs:

```bash
./gradlew testDebugUnitTest
```

This validates unit tests across modules.

### Build

Runs:

```bash
./gradlew assembleDebug :app:assembleRelease
```

This validates both debug and release assembly.

### Template smoke

The template smoke job validates generator behavior:

```bash
./gradlew help --task scaffoldFeature
./gradlew scaffoldFeature -PfeatureName=ci_feature
./gradlew :feature:ci_feature:presentation:compileDebugKotlin
./gradlew scaffoldFeature -PfeatureName=ci_database_feature -PwithDatabase=true
./gradlew :feature:ci_database_feature:data:compileDebugKotlin :feature:ci_database_feature:presentation:compileDebugKotlin
./gradlew create-new-app -Pargs='com.example.generated,GeneratedApp' -q --console=plain
```

It also verifies that generated apps do not copy local secrets or Git metadata.

## CI secrets setup

Each job creates local `secrets.properties` values needed for validation and build execution. These are CI-safe placeholder values used to exercise the build, not production secrets.

## Why generated output is tested

`scaffoldFeature` and `create-new-app` are core product features of ComposeTemplate. They can break independently of the current app.

The smoke job protects against regressions such as:

- scaffolded feature no longer compiles,
- database-enabled scaffold generates invalid Room files,
- app dependencies are not wired correctly,
- source directory rewrite breaks generated apps,
- local secret files are copied into generated output,
- generator task registration breaks.

## Checklist

- [ ] lint runs in CI.
- [ ] unit tests run in CI.
- [ ] debug and release builds run in CI.
- [ ] `scaffoldFeature` is tested.
- [ ] database-enabled scaffolding is tested.
- [ ] `create-new-app` is tested.
- [ ] generated apps exclude local secrets and Git metadata.
- [ ] CI commands are mirrored by local verification docs.

## Repository references

- `.github/workflows/ci.yml`
- `build-logic/convention/ScaffoldFeaturePlugin.kt`
- `build-logic/convention/CreateNewAppPlugin.kt`
- `build-logic/convention/ValidateSecretsPlugin.kt`
