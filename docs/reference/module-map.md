# Module Map

ComposeTemplate separates application composition, shared infrastructure, features, build logic, and performance tooling.

## Root modules

| Path | Purpose |
|---|---|
| `app` | Application module and composition root |
| `core` | Shared infrastructure modules |
| `feature` | Product feature modules |
| `build-logic` | Gradle convention plugins and generator tasks |
| `benchmark` | Macrobenchmark tests |
| `baselineprofile` | Baseline Profile generation |
| `docs` | MkDocs documentation source |

## Core modules

| Module | Purpose |
|---|---|
| `core:common` | Shared contracts, result types, constants, dispatchers |
| `core:data` | DataStore preferences and locale handling |
| `core:database` | Room database foundation |
| `core:network` | Retrofit, OkHttp, auth infrastructure, `BaseRepository` |
| `core:navigation` | Navigation manager, route contracts, screen registry |
| `core:ui` | Theme, shared UI, `BaseViewModel` |
| `core:secrets` | Secret access and native-backed secret loading |
| `core:security` | Runtime integrity signals and security reports |
| `core:analytics` | Analytics abstraction and implementation |
| `core:config` | App configuration and update contracts |
| `core:permission` | Runtime permission helpers |
| `core:google-play` | In-app review and update integrations |

## Feature modules

Each feature follows this structure:

```text
feature/{name}/
├── data
├── domain
├── navigation
└── presentation
```

Current template features:

| Feature | Role |
|---|---|
| `auth` | Full authentication example with repository and token refresh contracts |
| `splash` | Startup destination flow |
| `profile` | Preferences-backed profile, theme, and language example |
| `onboarding` | Medium-complexity onboarding flow |
| `home` | Minimal bottom-bar tab |
| `list` | Minimal list flow |
| `search` | Minimal search/filter example |
| `detail` | Parameterized detail route example |

## Dependency direction

The intended feature dependency direction is:

```text
data -> domain <- presentation
presentation -> navigation
navigation -> core:navigation
```

`core` modules should not depend on feature modules.
