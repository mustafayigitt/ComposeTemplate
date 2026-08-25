# CI for Android Template Repositories: Testing Generated Apps

## Who this article is for

This article is for maintainers of Android templates, starter kits, and platform repositories.

## What you will learn

- Why normal app CI is not enough for templates
- What a template smoke test should cover
- How generator regressions slip through
- How ComposeTemplate validates generated output

## The problem

A normal app repository can ask:

```text
Does this app build and pass tests?
```

A template repository must also ask:

```text
Does the app generated from this template build and behave correctly?
```

If CI only validates the template source, the generator can break unnoticed.

## ComposeTemplate CI model

ComposeTemplate’s CI validates:

- formatting with ktlint,
- static analysis with detekt,
- unit tests,
- debug and release assembly,
- feature scaffold generation,
- database-backed scaffold generation,
- compilation of scaffolded feature modules,
- create-new-app execution,
- generated app exclusion of local files.

## Why generated output matters

The generated app is what users actually consume. If it contains `local.properties`, stale package names, broken native bindings, or missing Gradle wiring, the template has failed.

## Smoke tests vs full validation

A smoke test is intentionally lightweight. It should catch high-value failures quickly. It does not replace deeper generated-app builds, security scans, or benchmark runs.

## Common mistakes

- Only building the template app.
- Not testing scaffolded features.
- Not checking local file exclusions.
- Not testing release assembly.
- Allowing generator behavior to change without review.

## Production checklist

- [ ] CI builds the template app.
- [ ] CI runs unit tests and static analysis.
- [ ] CI runs feature scaffolding.
- [ ] CI runs create-new-app.
- [ ] CI verifies generated apps do not include local secrets.
- [ ] CI compiles generated/scaffolded modules.

## Summary

Template CI must test the product of the template: generated projects. ComposeTemplate treats generator behavior as production behavior and validates it in CI.