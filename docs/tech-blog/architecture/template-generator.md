# Building a Production-Grade Jetpack Compose Template Generator

Most Android starter projects begin as sample apps. They demonstrate a stack, a folder structure, or a few architectural choices. That is useful, but it does not solve the repetitive work that every new production project needs: package renaming, module wiring, build configuration, CI, feature scaffolding, release hardening, and documentation.

ComposeTemplate approaches this differently. It is designed as a **template generator**, not just a sample application.

## The problem with sample apps

A sample app usually answers one question:

> How could this be implemented?

A production starter needs to answer a different question:

> How can I generate a real project from this structure and continue building without carrying template-specific baggage?

A sample app can tolerate manual setup. A generator cannot. A generator must be repeatable, predictable, and testable.

## What ComposeTemplate generates

ComposeTemplate gives a new Android project a ready foundation:

- Jetpack Compose UI
- Clean Architecture
- Feature-based multi-module structure
- Hilt dependency injection
- Navigation3 routing
- Retrofit/OkHttp networking
- Room and DataStore foundations
- Runtime and build-time security guardrails
- Static analysis
- CI
- Baseline Profile and Macrobenchmark modules
- Developer documentation

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

## Why generation matters

Renaming an Android project manually is fragile. Package names appear in Kotlin, XML, Gradle files, manifests, resources, and sometimes native bindings. ComposeTemplate automates this process and removes template-specific generator code from the generated app.

## Takeaway

A production-grade template is not just a folder structure. It is a system made of architecture, build logic, generators, tests, CI, security guardrails, and documentation.
