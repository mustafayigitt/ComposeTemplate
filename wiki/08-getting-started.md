# 08 - Getting Started (Code-Verified)

## Requirements

- JDK 17
- Android SDK: compileSdk 37, targetSdk 36, minSdk 26
- Gradle wrapper
- NDK 27/CMake only when secrets are retained

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
| `offline-first` | Network/auth plus Room |
| `local` | Room only; direct Home flow |
| `minimal` | No predefined network or database infrastructure |

For a network-backed app without native secrets/hardening, add `-PwithSecrets=false`. Local and minimal omit that network-dependent capability automatically.

## Start-flow behavior

- `remote` / `offline-first`: incomplete onboarding → Onboarding; otherwise stored user → Home, no user → Login.
- `local` / `minimal`: incomplete onboarding → Onboarding; otherwise → Home.

This is compile-time generated structure, not online/offline runtime branching.

## Secrets setup

Only when the generated README says secrets were included:

1. Copy the provided example secrets file to the local secrets file.
2. Fill the API keys, HTTPS base URLs, mask, signature hash, pinning options, and signing values.
3. Run the validation and hardening tasks documented by the generated project.

## Verify

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Add a feature

```bash
./gradlew scaffoldFeature -PfeatureName=user_profile
./gradlew :feature:user_profile:presentation:compileDebugKotlin
```

Every generated feature contains `data`, `domain`, `navigation`, and `presentation`. If the project selected a database strategy, `-PwithDatabase=true` also creates a Room starter entity and DAO.

## First-release checklist

- [ ] Release signing configured for the application
- [ ] Real backend configuration supplied for network strategies
- [ ] Secret validation and artifact scanning run when secrets are selected
- [ ] Persistence schema/migrations reviewed when Room is selected
- [ ] Sample features adapted or removed
- [ ] App icon, locales, metadata, and tests updated

---

[← Previous: 07 - Risks, Gaps and Open Questions](07-risks-and-gaps.md) · [Index](README.md) · *End of series*
