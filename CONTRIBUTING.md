# Contributing to ComposeTemplate

Thanks for considering a contribution. This project is a template/generator, not a single app —
changes here get copied into every project generated with `create-new-app`, so correctness and
consistency matter more than usual.

## Before You Start

For anything beyond a small fix (typo, doc correction, obvious bug), please open an issue first to
discuss the change. This avoids duplicated effort and lets us agree on approach before you invest
time — especially for anything touching `build-logic/`, navigation, or secret handling, where a
change ripples through every feature module.

All project documentation lives in [`wiki/`](wiki/README.md) — that is the single source of truth.
If your change alters behavior described there, update the relevant wiki page in the same PR.

## Branching & PRs

- Branch off `main` (or `develop` if it's currently active — check open PRs/issues if unsure).
- Target your PR at `main` or `develop`, matching whichever the CI workflow is validating
  (`.github/workflows/ci.yml` runs on PRs into both).
- Keep PRs focused: one logical change per PR. A convention-plugin change and a feature-module
  change are two PRs, not one.
- Write a PR description that explains *why*, not just *what* — the diff already shows what changed.

## Local Verification

Run the same checks CI runs before you push:

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

If you touched `build-logic/` (a convention plugin, `ScaffoldFeaturePlugin`, or `CreateNewAppPlugin`),
also run the template smoke checks locally:

```bash
./gradlew scaffoldFeature -PfeatureName=smoke_test
./gradlew :feature:smoke_test:presentation:compileDebugKotlin
./gradlew create-new-app -Pargs='com.example.smoketest,SmokeTest' -q --console=plain
```

Clean up any generated `feature/smoke_test` module and sibling `../SmokeTest` directory before
committing — these are throwaway verification artifacts, not part of the change.

You'll need a `secrets.properties` file to build locally — copy `secrets.properties.example` and
fill in placeholder values, then run `./gradlew validateSecrets` to confirm it's valid. See
[04 - Secrets, Security and Hardening](wiki/04-secrets-and-hardening.md) for the full key list and
validation rules, and [08 - Getting Started](wiki/08-getting-started.md) for first-time setup.

## Code Style

- Kotlin style is enforced by Ktlint and Detekt (`./gradlew ktlintCheck detekt`); run
  `./gradlew ktlintFormat` to auto-fix formatting issues.
- Follow the existing Clean Architecture layering: `data → domain ← presentation`, with
  `feature/*` depending on `core/*` and never the reverse. See
  [01 - Module Topology and Build System](wiki/01-module-topology.md) for the module graph and
  layering rules, and [02 - Navigation and UI State](wiki/02-navigation-and-ui-state.md) for the
  conventions new features are expected to follow (ViewModel structure, UiState/Event pattern,
  Route/UI composable separation).
- Prefer adding a new example to an existing feature module over inventing a new pattern — this
  repo doubles as a reference, so consistency across features is more valuable than local
  optimization.

## Adding or Changing a Feature Example

If your change affects one of the example features (`auth`, `detail`, `home`, `list`, `profile`,
`search`, `splash`, `onboarding`), keep its documented complexity tier in mind (see the feature
module table in [01 - Module Topology and Build System](wiki/01-module-topology.md)) — e.g. don't
add full Clean Architecture ceremony to a feature that's intentionally "Minimal" to demonstrate a
lean structure.

For generating a new vertical from scratch, see
[05 - Generator and Scaffolding Tooling](wiki/05-generator-and-scaffolding.md).

## Commit Messages

No strict format is enforced, but a short imperative summary line (`Add certificate pinning
validation to secrets check`) plus a body explaining *why* when the change isn't self-evident is
appreciated.

## Reporting Bugs / Requesting Features

Use [GitHub Issues](https://github.com/mustafayigitt/ComposeTemplate/issues). For security
vulnerabilities, do not open a public issue — follow the reporting guidance in
[04 - Secrets, Security and Hardening](wiki/04-secrets-and-hardening.md#reporting-a-vulnerability)
instead.
