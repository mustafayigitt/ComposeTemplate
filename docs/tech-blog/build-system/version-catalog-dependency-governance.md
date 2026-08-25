# Version Catalog and Dependency Governance in Android

## Who this article is for

This article is for Android developers maintaining projects with many modules and many dependencies.

## What you will learn

- Why dependency versions become a governance problem
- How Gradle Version Catalog works
- Why Compose BOM matters
- How Kotlin, KSP and AGP compatibility should be managed
- Common dependency-management mistakes

## The problem

In small projects, dependency versions are easy to see. In multi-module projects, versions can spread across many `build.gradle.kts` files.

That creates risk:

- different modules use different versions,
- KSP and Kotlin become incompatible,
- Compose libraries drift,
- dependency updates are hard to review,
- security and maintenance updates are missed.

## Version Catalog mental model

A Gradle Version Catalog centralizes dependency coordinates and versions in:

```text
gradle/libs.versions.toml
```

It usually contains:

- `[versions]` for version numbers,
- `[libraries]` for library aliases,
- `[plugins]` for plugin aliases.

Modules then reference aliases instead of hardcoding coordinates.

## ComposeTemplate approach

ComposeTemplate keeps AndroidX, Compose, Navigation3, Hilt, Room, Retrofit, OkHttp, testing, benchmark, static analysis, and build tool versions in the version catalog.

This is especially important because several tools must align:

- Kotlin and KSP,
- Android Gradle Plugin and Gradle,
- Compose compiler and Kotlin,
- Room compiler and KSP,
- Hilt compiler and KSP.

## Compose BOM

Compose uses a Bill of Materials. A BOM aligns Compose artifact versions so each Compose dependency does not need its own explicit version.

This reduces mismatch risk across UI dependencies.

## Renovate and update strategy

Automated tools can open update PRs, but version updates are still engineering changes. For Android projects, updates should consider build compatibility, runtime behavior, and generated template output.

## Common mistakes

### Updating Kotlin without KSP

KSP versions are tied to Kotlin. Updating one without the other can break compilation.

### Mixing Compose BOM with explicit Compose versions

That defeats part of the BOM’s purpose.

### Treating dependency updates as mechanical

Some updates change generated code, compiler behavior, lint rules, or runtime performance.

## Production checklist

- [ ] Versions are centralized in `libs.versions.toml`.
- [ ] Compose uses a BOM consistently.
- [ ] Kotlin and KSP are updated together.
- [ ] Dependency update PRs run full CI.
- [ ] Template smoke tests run after build-tool updates.

## Summary

A version catalog is not just a nicer syntax. It is dependency governance for a multi-module Android project. ComposeTemplate uses it to make upgrades visible, reviewable, and consistent.