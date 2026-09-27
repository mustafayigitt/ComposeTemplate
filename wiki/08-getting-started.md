# 08 - Getting Started (Code-Verified)

## Requirements

- JDK 17
- Android SDK with compileSdk 37, targetSdk 36, and minSdk 26 support
- Gradle wrapper
- NDK `27.0.12077973` and CMake only when native secrets are retained

## Generate an application

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate

./gradlew create-new-app \
  -Pargs='com.example.myapp,MyNewApp' \
  -PdataStrategy=remote \
  -q --console=plain

cd ../MyNewApp
```

Choose exactly one strategy:

| Value | Output |
| --- | --- |
| `remote` | Network/auth, no Room; recommended default |
| `offline-first` | Network/auth plus Room infrastructure |
| `local` | Room only; direct Home flow |
| `minimal` | No predefined network or database infrastructure |

For a network-backed app without native secrets/hardening, add `-PwithSecrets=false`. Local/minimal omit that network-dependent subsystem automatically.

`offline-first` provides both infrastructure families; you must still design synchronization, conflict resolution, cache policy, retry/background work, and migrations for the product.

## Generated start flow

- `remote` / `offline-first`: incomplete onboarding → Onboarding; otherwise stored user → Home and no user → Login.
- `local` / `minimal`: incomplete onboarding → Onboarding; otherwise → Home.

This is compile-time source projection, not online/offline runtime branching.

## Configure the selected output

- For network strategies, replace sample/placeholder backend configuration with product endpoints and contracts.
- If secrets were retained, create the documented local secrets file and run validation/hardening tasks.
- Configure release signing for the generated application.
- For Room strategies, review schema ownership, migrations, backup/export policy, and test coverage.

NDK/XOR hardening does not make client-shipped values secret; keep real secrets and authorization decisions server-side.

## Verify

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Add a feature

```bash
./gradlew scaffoldFeature -PfeatureName=user_profile
./gradlew :feature:user_profile:presentation:compileDebugKotlin
```

Every feature contains `data`, `domain`, `navigation`, and `presentation`. In a database-backed output, add `-PwithDatabase=true` to generate a starter Room entity and DAO.

## First-release checklist

- [ ] Product domain behavior and backend contracts implemented
- [ ] Release signing and CI credentials configured
- [ ] Real backend configuration supplied for network strategies
- [ ] Secret validation and artifact scanning run when secrets are retained
- [ ] Room schemas, migrations, and synchronization policy reviewed where applicable
- [ ] Sample features adapted or removed
- [ ] Branding, icon, locales, privacy/metadata, and permissions reviewed
- [ ] ViewModel, navigation, UI, instrumentation, and release smoke tests added

---

[← Previous: 07 - Risks, Gaps and Open Questions](07-risks-and-gaps.md) · [Index](README.md) · *End of series*
