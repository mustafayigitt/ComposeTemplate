# CI for Android Template Repositories: Testing Generated Apps

A template repository needs a different CI mindset than a normal app repository.

## Problem

If CI only builds the current app, generator regressions can slip through. A template must prove that the output it generates still works.

## ComposeTemplate approach

The CI pipeline checks:

- ktlint and detekt,
- unit tests,
- debug and release builds,
- feature scaffold generation,
- database-backed feature generation,
- generated feature compilation,
- create-new-app execution,
- local secret exclusion in generated apps.

## Takeaway

For a template repository, CI must test the template output, not only the template source.
