# Contributing to ComposeTemplate

ComposeTemplate is a generator, not a single app. Changes can affect the source fixture, generated consumers, or both.

## Before you start

Open an issue before substantial work, especially for `build-logic`, navigation, data-strategy projection, secrets, module boundaries, or generated-project structure. Documentation lives in [`wiki/`](wiki/README.md); behavior and docs must change together.

## Branches and pull requests

- Branch from `main` unless an active issue explicitly targets another branch.
- Keep each PR focused on one logical outcome.
- Explain why the change is needed and which generated configurations it affects.
- Do not merge until required checks are green and review findings are resolved.

## Local verification

Run the source-template gates:

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

For build-logic or generation changes, test the affected consumer strategies explicitly. Examples:

```bash
./gradlew create-new-app \
  -Pargs='com.example.remote,RemoteApp' \
  -PdataStrategy=remote \
  -q --console=plain

./gradlew create-new-app \
  -Pargs='com.example.minimal,MinimalApp' \
  -PdataStrategy=minimal \
  -q --console=plain
```

If secrets projection changed, also generate a network-backed secret-free consumer:

```bash
./gradlew create-new-app \
  -Pargs='com.example.secretfree,SecretFreeApp' \
  -PdataStrategy=remote \
  -PwithSecrets=false \
  -q --console=plain
```

Build every generated consumer you test:

```bash
./gradlew assembleDebug :app:assembleRelease
```

Clean generated sibling projects before committing.

## Feature scaffolding

```bash
./gradlew scaffoldFeature -PfeatureName=smoke_test
./gradlew :feature:smoke_test:presentation:compileDebugKotlin
```

For Room scaffolding:

```bash
./gradlew scaffoldFeature \
  -PfeatureName=database_smoke_test \
  -PwithDatabase=true
./gradlew :feature:database_smoke_test:data:compileDebugKotlin
```

Remove generated smoke-test feature folders before committing.

## Secrets for the source template

The full source template retains secrets. Copy `secrets.properties.example`, supply non-placeholder local values, and run `./gradlew validateSecrets`. Never commit `secrets.properties` or a production keystore. NDK/XOR is hardening and obfuscation, not secure client-side secret storage.

## Architecture rules

- Every retained feature has `data`, `domain`, `navigation`, and `presentation` modules.
- Modules are discovered from folders; do not add manual `include(...)` entries.
- `:app` may import only `core:common`, `core:navigation`, and `core:ui`.
- Always-present core modules may name only one another.
- Optional core modules may depend on core, never on features.
- A feature may reference another feature only through its navigation contract.
- Use DI multibindings for optional integrations instead of importing removable implementations.

Boundary tasks run during normal build/test lifecycles. Narrow exceptions belong in the importing module's own `moduleBoundary` block.

## Code and documentation style

Run `./gradlew ktlintCheck detekt`; use `./gradlew ktlintFormat` for Kotlin formatting. Update the relevant wiki page, root README, generated-consumer wording, and CI contract whenever behavior changes.

Use GitHub Issues for bugs/features. Report vulnerabilities through a private GitHub security advisory, not a public issue.
