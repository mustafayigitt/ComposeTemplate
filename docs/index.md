# ComposeTemplate

ComposeTemplate is a production-grade Jetpack Compose template generator for starting Android projects with architecture, build tooling, CI, security hardening, performance infrastructure, and feature scaffolding already in place.

It is not only a sample app. It is a working template that can generate a new project with your package name, app name, module graph, build conventions, and quality gates.

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

Create `secrets.properties`, then validate and build:

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Documentation paths

Start with these pages:

1. [Getting Started](getting-started.md)
2. [Project Philosophy](project-philosophy.md)
3. [Generate a New App](guides/generate-new-app.md)
4. [Architecture Overview](architecture/overview.md)
5. [Convention Plugins](build-system/convention-plugins.md)
6. [Secret Management](security/secret-management.md)
7. [Release Readiness](guides/release-readiness.md)

## How to read this documentation

ComposeTemplate documentation is organized into four layers:

| Area | Purpose |
|---|---|
| Guides | Complete concrete workflows |
| Architecture | Explain design decisions and module boundaries |
| Reference | Look up tasks, modules, plugins, and configuration |
| Tech Blog | Read long-form engineering deep dives |

If you want to use the template quickly, start with Guides.

If you want to understand why the project is structured this way, read Architecture and Build System.

If you want publishable engineering articles, read the Tech Blog series.
