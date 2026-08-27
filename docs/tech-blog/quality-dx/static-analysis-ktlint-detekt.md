# Static Analysis with Ktlint and Detekt

## Who this article is for

This article is for Android developers who want consistent style and quality checks across multi-module projects.

## What you will learn

- why static analysis drifts in modular projects
- what Ktlint and Detekt each provide
- why static analysis belongs in build logic
- how generated code should meet the same quality bar

## The problem

As Android projects grow, formatting and static analysis rules often become inconsistent. Some modules apply checks, others skip them, and generated code may not follow the same rules as hand-written code.

That creates avoidable review noise and quality drift.

## Why this matters for Android projects

Static analysis is not only about style. It creates a shared baseline for maintainability. In a template repository, that baseline becomes the starting point for every generated app.

## ComposeTemplate's approach

ComposeTemplate uses:

- Ktlint for formatting/style checks
- Detekt for static analysis
- shared Detekt config under `config/detekt`
- `composetemplate.static.analysis` convention plugin
- CI lint job running both checks

## Generated code quality

Generated feature code must pass the same checks as normal source. If scaffold output violates a rule, the generator should be fixed rather than relying on suppressions.

## Design trade-offs

Strict static analysis can feel slower early in development, but it prevents style drift and keeps code review focused on behavior.

## Production checklist

- [ ] Ktlint runs locally and in CI
- [ ] Detekt runs locally and in CI
- [ ] Detekt config is versioned
- [ ] generated code passes checks
- [ ] suppressions are narrow and justified
- [ ] convention plugins apply analysis consistently

## Takeaways

- Static analysis is a project consistency tool.
- Generated code should meet the same bar as hand-written code.
- Centralized configuration avoids module-level drift.
