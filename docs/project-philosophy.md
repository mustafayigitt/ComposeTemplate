# Project Philosophy

ComposeTemplate is built around one idea:

> Starting a new Android project should not require rebuilding architecture, build logic, security checks, quality gates, performance infrastructure, and documentation foundations from scratch.

## Template generator, not just starter app

A starter app shows one possible implementation.

A template generator produces a new project with a new package name, app name, source directory structure, build setup, and project identity.

That distinction matters because real projects should not remain tied to the original template identity. They need to become their own applications while keeping the template's engineering foundation.

ComposeTemplate therefore treats `create-new-app` as a core capability, not a convenience script.

## Principles

### 1. Boundaries should be visible at build level

Package names and folder names are useful, but they are not enough. If every layer lives in the same Gradle module, almost anything can import anything else.

ComposeTemplate uses Gradle modules to make boundaries visible and harder to misuse.

### 2. Architecture should be repeatable

Every feature follows the same high-level shape:

```text
feature/{name}/
├── data
├── domain
├── navigation
└── presentation
```

This gives contributors a known place for repository contracts, implementations, routes, ViewModels, UI state, and screens.

### 3. Build logic should be centralized

Multi-module Android projects quickly drift when every module repeats its own Gradle setup.

ComposeTemplate uses convention plugins so each module declares its role instead of reimplementing boilerplate build configuration.

### 4. Generator behavior must be tested

A template repository is not healthy just because the current app builds.

It must also prove that generated apps and generated features compile. ComposeTemplate's CI includes template smoke tests for this reason.

### 5. Security claims must be honest

Client-side secrets are never truly secret. Runtime checks can be bypassed. Certificate pinning can create operational risk.

ComposeTemplate still includes hardening tools, but documents them as cost-increasing guardrails, not absolute security boundaries.

### 6. Performance should be a foundation

Startup performance and critical-flow measurement should not be postponed until late in the project.

ComposeTemplate includes Baseline Profile and Macrobenchmark infrastructure from the beginning.

## Trade-offs

ComposeTemplate intentionally accepts some upfront cost:

- more Gradle modules,
- more explicit wiring,
- more build-logic code,
- NDK/CMake requirements for native secret support,
- a steeper learning curve than a minimal sample app,
- generator code that must be maintained.

These costs are acceptable for projects that are expected to grow. The payoff is clearer boundaries, repeatable feature creation, safer refactoring, and better release confidence.

## What ComposeTemplate optimizes for

ComposeTemplate optimizes for:

- medium and large Android apps,
- teams that care about architecture consistency,
- projects that need security and release guardrails,
- maintainers building internal templates,
- developers who want production patterns visible from day one.

It does not optimize for the smallest possible demo project.
