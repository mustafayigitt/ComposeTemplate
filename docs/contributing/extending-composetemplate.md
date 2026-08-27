# Extending ComposeTemplate

ComposeTemplate is designed to be extended, but extensions should preserve the template's core promise: generated projects should remain consistent, buildable, and production-oriented.

## Extension areas

Common extension points include:

| Area | Examples |
|---|---|
| Core modules | analytics, config, permissions, database, UI foundations |
| Convention plugins | new module roles, shared compiler setup, validation rules |
| Feature scaffold | additional generated files, optional feature capabilities |
| App generator | rewrite rules, exclusion rules, post-generation cleanup |
| Security tooling | new validation checks, hardening report fields |
| CI | additional smoke tests, generated-app validation, documentation build checks |
| Documentation | new guides, reference pages, and deep-dive articles |

## Extension principles

### Keep generated output buildable

Any extension that affects `create-new-app` or `scaffoldFeature` should be validated through CI. A template change is not complete until generated output still compiles.

### Prefer role-specific build logic

Avoid one large convention plugin that configures unrelated behavior. Keep plugin names aligned with module roles.

### Document new behavior

If a new task, convention plugin, module, or generated file is added, update the relevant docs and reference pages in the same change.

### Do not weaken boundaries

Extensions should preserve the intended dependency direction:

```text
data -> domain <- presentation
presentation -> navigation
navigation -> core:navigation
```

## Generator-impacting changes

If you change generator behavior, update or verify:

- `CreateNewAppPlugin.kt`,
- `ScaffoldFeaturePlugin.kt`,
- `settings.gradle.kts` wiring behavior,
- `app/build.gradle.kts` dependency insertion behavior,
- CI template smoke checks,
- docs under `guides/`, `template-tools/`, and `reference/`.

## Validation checklist

- [ ] current app builds.
- [ ] generated feature compiles.
- [ ] generated database feature compiles if relevant.
- [ ] generated app excludes local secrets and Git metadata.
- [ ] docs describe the new behavior.
- [ ] release/security-impacting changes run hardening checks.

## Related documentation

- [Documentation Standard](documentation-standard.md)
- [Gradle Convention Plugins](../build-system/gradle-convention-plugins.md)
- [CI Pipeline](../build-system/ci-pipeline.md)
- [Create New App](../template-tools/create-new-app.md)
