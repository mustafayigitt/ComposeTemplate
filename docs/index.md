# ComposeTemplate

ComposeTemplate is a production-grade Jetpack Compose template generator for starting Android projects with architecture, build tooling, CI, security hardening, performance infrastructure, and feature scaffolding already in place.

It is not only a sample app. It is a working template that can generate a new project with your package name, app name, module graph, build conventions, and quality gates.

## Start here

If you are new to the project, follow this path:

1. [Getting Started](getting-started.md)
2. [Generate a New App](guides/generate-new-app.md)
3. [Configure Secrets](security/secret-management.md)
4. [Run Release Readiness Checks](guides/release-readiness.md)
5. [Scaffold a Feature](guides/scaffold-feature.md)

## What ComposeTemplate provides

ComposeTemplate brings together the foundations most Android projects eventually need:

- Jetpack Compose UI with Material 3
- Clean Architecture enforced through Gradle module boundaries
- feature-based multi-module structure
- Hilt dependency injection
- Navigation3-based type-safe navigation
- Retrofit and OkHttp network infrastructure
- Room and DataStore foundations
- native secret obfuscation with NDK/CMake support
- runtime integrity signals
- Gradle convention plugins
- one-command app generation
- feature scaffolding
- Ktlint and Detekt static analysis
- CI that validates both the current app and generated output
- Baseline Profile and Macrobenchmark modules
- MkDocs-based documentation publishing

## Why this project exists

Starting a production Android project often means rebuilding the same foundations repeatedly:

- module boundaries,
- dependency injection,
- navigation,
- network setup,
- secret handling,
- release hardening,
- static analysis,
- CI,
- performance measurement,
- documentation structure.

ComposeTemplate turns those foundations into a reusable generator.

The goal is not to hide complexity. The goal is to make important decisions explicit, repeatable, and reviewable from day one.

## Quick start

Clone the template:

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
```

Generate a new app:

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
cd ../MyNewApp
```

Validate after configuring secrets:

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Documentation map

| Area | Purpose |
|---|---|
| Guides | Complete concrete workflows |
| Architecture | Design decisions and module boundaries |
| Build System | Convention plugins, version catalog, static analysis, CI |
| Security | Secrets, runtime integrity, pinning, release hardening |
| Template Tools | Generator and scaffold task references |
| Quality | Testing, release readiness, performance foundations |
| Reference | Task and module lookup |
| Tech Blog | Long-form engineering deep dives |

## Recommended deep dives

- [Architecture Overview](architecture/overview.md)
- [Feature Modularization](architecture/feature-modularization.md)
- [Gradle Convention Plugins](build-system/gradle-convention-plugins.md)
- [Secret Management](security/secret-management.md)
- [Tech Blog Series](tech-blog/index.md)
