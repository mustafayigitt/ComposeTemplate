# Static Analysis

ComposeTemplate includes static analysis as a first-class build-system concern.

The goal is to make formatting and code-quality checks consistent across modules and CI.

## Problem

In a multi-module project, static analysis can drift when each module configures tools independently.

Common problems include:

- inconsistent formatting rules,
- Detekt enabled in some modules but not others,
- local checks differing from CI checks,
- generated modules missing quality gates,
- suppressions becoming unclear.

ComposeTemplate centralizes this through build logic and shared config.

## Tools

ComposeTemplate uses:

| Tool | Purpose |
|---|---|
| Ktlint | Kotlin formatting and style checks |
| Detekt | static analysis and code smell detection |

## Convention plugin

Static analysis is configured through:

```text
composetemplate.static.analysis
```

The implementation lives in build logic and applies consistent rules across modules.

## Shared configuration

Detekt configuration is stored under:

```text
config/detekt/detekt.yml
```

This keeps rule decisions versioned and reviewable.

## Local commands

Run:

```bash
./gradlew ktlintCheck
./gradlew detekt
```

These are also part of the common verification set:

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## CI integration

The CI pipeline has a dedicated lint job that runs both Ktlint and Detekt. This ensures style and analysis failures are caught before merge.

## Generated code considerations

Because `scaffoldFeature` creates source files, generated code should follow the same formatting and static analysis expectations as hand-written code.

If generated code repeatedly violates a rule, fix the generator rather than applying broad suppressions.

## Suppression policy

Suppressions should be narrow and justified. Prefer changing code or generator output before disabling rules globally.

## Checklist

- [ ] `ktlintCheck` runs locally and in CI.
- [ ] `detekt` runs locally and in CI.
- [ ] Detekt configuration is versioned.
- [ ] generated feature code passes static analysis.
- [ ] suppressions are narrow and documented.
- [ ] formatting failures are fixed at source, not ignored.

## Repository references

- `build-logic/convention/StaticAnalysisConventionPlugin.kt`
- `config/detekt/detekt.yml`
- `.github/workflows/ci.yml`
