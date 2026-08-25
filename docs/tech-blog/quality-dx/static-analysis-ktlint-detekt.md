# Static Analysis with Ktlint and Detekt

## Who this article is for

This article is for Android teams that want consistent style and quality checks without relying only on code review comments.

## What you will learn

- What Ktlint and Detekt solve
- Why static analysis should be centralized
- How CI turns quality rules into gates
- Common mistakes in rule management

## The problem

Without automation, teams spend review time on formatting, naming, complexity, and preventable code smells. Reviews become inconsistent because each reviewer has different preferences.

## Ktlint vs Detekt

Ktlint focuses on Kotlin formatting and style consistency.

Detekt focuses on static analysis: complexity, potential bugs, maintainability issues, and project-specific rules.

They are complementary.

## ComposeTemplate approach

ComposeTemplate uses:

```bash
./gradlew ktlintCheck
./gradlew detekt
```

Static analysis is wired through convention plugins so new modules inherit the same quality gates.

## Why convention plugins matter

If static analysis is configured manually per module, new modules can forget to apply it. Centralized build logic makes quality checks part of the project platform.

## Common mistakes

- Adding too many strict rules at once.
- Suppressing warnings without explanation.
- Letting generated code trigger irrelevant checks.
- Running checks locally but not in CI.

## Production checklist

- [ ] Ktlint runs in CI.
- [ ] Detekt runs in CI.
- [ ] Rules are documented.
- [ ] Suppressions are justified.
- [ ] New modules inherit checks automatically.
- [ ] Generated code handling is defined.

## Summary

Static analysis is not about perfection. It is about making quality consistent, automated, and reviewable across a growing Android codebase.