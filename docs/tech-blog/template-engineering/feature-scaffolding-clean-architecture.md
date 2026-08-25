# Feature Scaffolding for Clean Architecture

## Who this article is for

This article is for teams that repeatedly create similar feature modules and want to reduce boilerplate without weakening architecture.

## What you will learn

- What feature scaffolding should generate
- How scaffolding supports Clean Architecture
- Why generated code should be treated as a starting point
- Common scaffold-generator mistakes

## The problem

Adding a feature in a modular Clean Architecture project is repetitive. A developer may need to create:

- `data`, `domain`, `navigation`, and `presentation` modules,
- Gradle files,
- settings entries,
- route objects,
- screen providers,
- ViewModels,
- state and event models,
- Hilt modules,
- optional database classes.

Manual work is slow and error-prone.

## ComposeTemplate approach

ComposeTemplate provides:

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

The generated feature follows the same module structure used by existing features:

```text
feature/settings/
├── data/
├── domain/
├── navigation/
└── presentation/
```

## Why scaffolding helps

Scaffolding makes the intended architecture the easiest path. Instead of asking developers to remember every convention, the generator creates a consistent baseline.

## Generated code is not final code

Scaffolded code should compile and demonstrate the pattern. Developers should still adapt it to actual domain behavior, UI requirements, validation rules, and data sources.

## Common mistakes

- Generating too much business logic.
- Generating code that does not compile.
- Forgetting settings or app module wiring.
- Creating scaffolds that differ from real project conventions.

## Production checklist

- [ ] Scaffolded feature compiles.
- [ ] Generated modules follow architecture boundaries.
- [ ] Optional database scaffold is valid.
- [ ] CI tests scaffold output.
- [ ] Generated code is documented as a starting point.

## Summary

Feature scaffolding improves developer experience by turning architecture conventions into executable tooling. ComposeTemplate uses scaffolding to make correct feature creation faster and less error-prone.