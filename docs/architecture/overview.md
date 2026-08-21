# Architecture Overview

ComposeTemplate separates app composition, shared infrastructure, product features, build logic, and performance tooling.

```text
ComposeTemplate/
├── app/
├── core/
├── feature/
├── build-logic/
├── benchmark/
└── baselineprofile/
```

## App module

The `app` module is the composition root. It connects feature modules, configures app-level setup, owns application id/namespace, and should not contain business logic.

## Core modules

`core/*` modules provide reusable infrastructure: common result types, data, database, network, secrets, security, UI, navigation, analytics, config, permissions, and Google Play integrations.

## Feature modules

Each feature is split into four Gradle modules:

```text
feature/{name}/
├── data
├── domain
├── navigation
└── presentation
```

## Dependency direction

```text
data → domain ← presentation
navigation → core:navigation
presentation → navigation
```

The domain layer should not know data or presentation implementation details.
