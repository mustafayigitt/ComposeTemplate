# Documentation Standard

ComposeTemplate documentation should read like production engineering documentation, not a collection of notes.

Every page must help a reader understand one of three things:

1. how to use the template,
2. why the template is designed this way,
3. how to extend or operate the generated project safely.

## Page types

### Guide

Guides are task-oriented. They should help a developer complete a concrete workflow.

Required structure:

```markdown
# Title

## What this guide covers
## Prerequisites
## Steps
## Validation
## Troubleshooting
## Related documentation
```

Use guides for workflows such as generating a new app, scaffolding a feature, configuring secrets, running checks, or preparing a release.

### Architecture document

Architecture documents explain design decisions and boundaries.

Required structure:

```markdown
# Title

## Problem
## Design goals
## How ComposeTemplate solves it
## Module boundaries
## Implementation walkthrough
## Trade-offs
## Checklist
## Related documentation
```

Architecture pages should include concrete module names and explain why the boundary exists.

### Reference document

Reference documents are lookup-oriented.

Required structure:

```markdown
# Title

## Purpose
## Reference table
## Notes
## Related documentation
```

Use references for Gradle tasks, convention plugin maps, configuration properties, and troubleshooting matrices.

### Tech blog article

Tech blog articles are long-form engineering deep dives.

Required structure:

```markdown
# Title

## Who this article is for
## What you will learn
## The problem
## Why this matters for Android projects
## Common approaches
## ComposeTemplate's approach
## Implementation walkthrough
## Design trade-offs
## Production checklist
## Takeaways
```

A tech blog article must teach the underlying engineering topic, not only describe that ComposeTemplate uses it.

## Quality bar

A page is not ready if it only lists tools or checklist items. Prefer explaining:

- the problem being solved,
- the failure mode in real Android projects,
- the ComposeTemplate design decision,
- where the implementation lives,
- what trade-offs the decision introduces,
- how a reader can validate the setup.

## Code examples

Use code examples when they clarify a boundary or workflow.

Good examples:

- Gradle dependency snippets,
- Kotlin interface/implementation pairs,
- command-line examples,
- CI job snippets,
- directory structure examples.

Avoid examples that are not present in, or supported by, the repository.

## Repository references

When a page explains a feature, include relevant files or modules.

Example:

```markdown
## Repository references

- `build-logic/convention/src/main/kotlin/.../ScaffoldFeaturePlugin.kt`
- `feature/auth/domain`
- `feature/auth/data`
- `core/navigation`
```

## Accuracy rule

Documentation must follow the codebase. Do not claim a class, task, plugin, or feature exists unless it exists in the repository or is explicitly marked as planned.

If the documentation describes a recommended future improvement, label it clearly as a recommendation.

## Tone

Use a professional, direct engineering tone.

Prefer:

> ComposeTemplate keeps repository contracts in `domain` and implementations in `data` so dependency direction is enforced by Gradle modules.

Avoid:

> This is an awesome way to organize code.

## Checklist style

Checklists should be actionable and reviewable.

Prefer:

```markdown
- [ ] `domain` does not depend on `data`.
- [ ] DTOs are mapped before reaching Presentation.
```

Avoid:

```markdown
- [ ] Architecture is good.
```

## Deep-dive expectations

For high-value topics such as Clean Architecture, Navigation3, convention plugins, template generation, secrets, CI, Baseline Profiles, and Macrobenchmark, write at deep-dive level.

A deep-dive page should usually include:

- context,
- problem statement,
- implementation walkthrough,
- real repository references,
- common mistakes,
- trade-offs,
- production checklist.
