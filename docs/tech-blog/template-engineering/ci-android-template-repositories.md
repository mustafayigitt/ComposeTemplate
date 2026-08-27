# CI for Android Template Repositories

## Who this article is for

This article is for maintainers building Android starter kits, project generators, internal mobile platforms, or reusable app templates.

## What you will learn

- why normal app CI is not enough for template repositories
- what generated-output smoke tests should cover
- how ComposeTemplate validates scaffolding and app generation
- why secret exclusion belongs in CI

## The problem

A normal Android repository usually validates the current app:

- lint
- unit tests
- debug build
- release build

A template repository has another responsibility: it must prove the generated output works.

The template app can build while the generator is broken. A scaffold task can emit invalid modules. A create-new-app task can accidentally copy secrets. A package rewrite can miss source paths.

That is why template repositories need CI beyond normal app checks.

## Why this matters for Android projects

A template generator creates the starting point for future applications. If the generator is broken, every new project starts broken.

This is worse than a normal app bug because it spreads into downstream repositories.

## ComposeTemplate's CI shape

ComposeTemplate CI has four major jobs:

1. lint
2. unit tests
3. debug and release build
4. template smoke test

The first three validate the current repository. The fourth validates the generator behavior.

## Template smoke test

The smoke test checks:

```bash
./gradlew help --task scaffoldFeature
./gradlew scaffoldFeature -PfeatureName=ci_feature
./gradlew :feature:ci_feature:presentation:compileDebugKotlin
./gradlew scaffoldFeature -PfeatureName=ci_database_feature -PwithDatabase=true
./gradlew :feature:ci_database_feature:data:compileDebugKotlin :feature:ci_database_feature:presentation:compileDebugKotlin
./gradlew create-new-app -Pargs='com.example.generated,GeneratedApp' -q --console=plain
```

It then verifies that generated apps do not include local secret files or Git metadata.

## What this catches

The smoke test protects against:

- scaffold task registration failures
- invalid generated Gradle files
- invalid generated ViewModel or screen code
- invalid generated Room starter files
- missing app dependency wiring
- broken package rewrite behavior
- copied `secrets.properties`
- copied `local.properties`
- copied `.git` metadata

## Design trade-offs

Template smoke tests add CI time, but they protect the most important behavior of a generator repository.

For ComposeTemplate, this is not optional. `create-new-app` and `scaffoldFeature` are product features of the template.

## Production checklist

- [ ] current app lint is checked
- [ ] current app tests run
- [ ] debug and release builds run
- [ ] feature scaffolding is smoke-tested
- [ ] database scaffolding is smoke-tested
- [ ] generated app creation is smoke-tested
- [ ] generated apps exclude local secrets
- [ ] CI mirrors documented local verification commands

## Takeaways

- Template repositories must test generated output.
- A successful current-app build does not prove the generator works.
- Secret exclusion should be validated automatically.
- CI is part of the template's product quality.
