# Contribution Guide

A contribution should protect both the current template app and the generated output.

ComposeTemplate is not only source code. It is also a generator, a set of architecture conventions, a build system, and documentation for downstream projects.

## Before opening a PR

Run the checks that match your change.

### Common local verification

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug
```

### Release or security-impacting changes

```bash
./gradlew validateSecrets :app:assembleRelease scanApkForSecrets
```

### Generator-impacting changes

```bash
./gradlew scaffoldFeature -PfeatureName=test_feature
./gradlew :feature:test_feature:presentation:compileDebugKotlin
./gradlew create-new-app -Pargs='com.example.generated,GeneratedApp' -q --console=plain
```

If database scaffolding changes, also validate:

```bash
./gradlew scaffoldFeature -PfeatureName=test_database_feature -PwithDatabase=true
./gradlew :feature:test_database_feature:data:compileDebugKotlin
```

## Contribution rules

- Do not add business logic to the `app` module.
- Keep domain modules independent from Android UI and infrastructure details.
- Do not leak DTOs into UI state.
- Do not commit real secrets.
- Do not log tokens, API keys, or sensitive headers.
- Keep generated code aligned with hand-written architecture.
- Update docs when behavior, commands, modules, or conventions change.

## Documentation changes

Documentation should follow [Documentation Standard](documentation-standard.md).

Prefer clear explanations, repository references, validation steps, and actionable checklists.

## PR checklist

- [ ] relevant Gradle checks pass.
- [ ] generated output is validated when generator behavior changes.
- [ ] docs are updated for behavior changes.
- [ ] new modules use the correct convention plugins.
- [ ] security-impacting changes include release-readiness validation.
