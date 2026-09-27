# ComposeTemplate

A **CI-proven Jetpack Compose project generator** for Android. It produces a multi-module Clean Architecture starter whose network, authentication, persistence, and secrets infrastructure is selected at generation time.

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
./gradlew create-new-app \
  -Pargs='com.example.myapp,MyNewApp' \
  -PdataStrategy=remote \
  -q --console=plain
```

## Data strategies

| Strategy | Network/auth | Room | Generated start flow |
| --- | ---: | ---: | --- |
| `remote` | Yes | No | Onboarding → Login/Home |
| `offline-first` | Yes | Yes | Onboarding → Login/Home |
| `local` | No | Yes | Onboarding → Home |
| `minimal` | No | No | Onboarding → Home |

`dataStrategy` is strict and defaults to `remote`. Network-backed outputs retain secrets/native hardening by default; add `-PwithSecrets=false` to omit it. Local and minimal outputs omit secrets/security automatically.

Every retained feature always contains `data`, `domain`, `navigation`, and `presentation`. Add another feature with:

```bash
./gradlew scaffoldFeature -PfeatureName=user_profile
```

Room-backed generated projects can also scaffold starter entity/DAO files with `-PwithDatabase=true`.

## What the generator guarantees

CI generates and assembles all four strategies in debug and release, checks strategy-specific path/text residue, proves a network-backed secret-free output, rejects invalid strategy values, compiles regular and Room feature scaffolds, and rebuilds the source template after deleting optional modules.

## Scope

ComposeTemplate is an architecture starter, not a finished product. Generated apps still need product-specific domain logic, real backend contracts, signing, branding, migrations, and runtime/UI tests. The `offline-first` strategy currently selects network + Room topology; it does not generate synchronization, conflict-resolution, or cache-policy logic.

## Documentation

The [`wiki/`](wiki/README.md) directory is the documentation source of truth:

- [00 - Project Context](wiki/00-project-context.md)
- [01 - Module Topology and Build System](wiki/01-module-topology.md)
- [02 - Navigation and UI State](wiki/02-navigation-and-ui-state.md)
- [03 - Network and Auth Token Flow](wiki/03-network-and-auth.md)
- [04 - Secrets, Security and Hardening](wiki/04-secrets-and-hardening.md)
- [05 - Generator and Scaffolding Tooling](wiki/05-generator-and-scaffolding.md)
- [06 - Quality, Tests and CI](wiki/06-quality-tests-ci.md)
- [07 - Risks, Gaps and Open Questions](wiki/07-risks-and-gaps.md)
- [08 - Getting Started](wiki/08-getting-started.md)

## Requirements

JDK 17 and Android SDK (compileSdk 37 / targetSdk 36 / minSdk 26). NDK 27 with CMake is required only when secrets are retained. Always use the Gradle wrapper.

See [CONTRIBUTING.md](CONTRIBUTING.md). Licensed under Apache-2.0.
