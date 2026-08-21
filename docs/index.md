# ComposeTemplate

ComposeTemplate is a production-grade Jetpack Compose template generator for starting new Android projects with architecture, build tooling, CI, security hardening, and feature scaffolding already in place.

## Core capabilities

- Jetpack Compose UI
- Clean Architecture
- Feature-based multi-module architecture
- Hilt dependency injection
- Navigation3-based type-safe navigation
- Retrofit + OkHttp network layer
- Room and DataStore foundations
- Native secret obfuscation
- Runtime security signals
- Gradle convention plugins
- Feature scaffolding
- One-command app generation
- Ktlint + Detekt static analysis
- CI pipeline with template smoke tests
- Baseline Profile and Macrobenchmark modules

## Quick start

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
cd ../MyNewApp
```

Then run:

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Recommended reading

1. [Getting Started](getting-started.md)
2. [Project Philosophy](project-philosophy.md)
3. [Architecture Overview](architecture/overview.md)
4. [Secret Management](security/secret-management.md)
5. [Create New App](template-tools/create-new-app.md)
6. [Release Readiness Checklist](quality/release-readiness-checklist.md)
