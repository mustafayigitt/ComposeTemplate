# Version Catalog and Dependency Governance in Android

Dependency management becomes a governance problem in multi-module Android projects. ComposeTemplate centralizes versions through Gradle Version Catalog.

## Problem

Hardcoded versions spread across modules create drift. Kotlin, KSP, AGP, Compose, Hilt, and Room compatibility must be managed together.

## ComposeTemplate approach

All versions live in:

```text
gradle/libs.versions.toml
```

The catalog groups SDK, AndroidX, Compose, Navigation, Network, Hilt, Kotlin, testing, static analysis, benchmarking, and build tooling versions.

## Takeaway

A version catalog is not just cleanup. It is the dependency governance layer of a modern Android project.
