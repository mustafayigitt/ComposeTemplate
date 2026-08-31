# 08 - Getting Started (Code-Verified)

Every command and key below was verified against the Gradle plugins and CI workflow, not against a previous README.

## Toolchain requirements (derived from the build)

| Requirement | Source of truth |
| --- | --- |
| JDK 17 | `.github/workflows/ci.yml` (temurin 17) |
| Android SDK: compileSdk 37, targetSdk 36, minSdk 26 | `gradle/libs.versions.toml` |
| NDK 27.0.12077973 + CMake | version catalog + `core/secrets` |
| Gradle wrapper (do not use a system Gradle) | composite build with `includeBuild("build-logic")` |

The app targets Android 8.0 (API 26) and newer. Lowering that baseline is a one-line
change in the version catalog, but read the [baseline decision log](07-risks-and-gaps.md#baseline-decision-log)
first — some of the template's Java-API assumptions depend on it.

## 1. Generate your app

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
cd ../MyNewApp
```

Argument format is `packageName,AppName`. The output is a **sibling** directory, with no `.git`, no `local.properties` and no `secrets.properties` carried over.

## 2. Create `secrets.properties`

Keys below are exactly those read by `ValidateSecretsPlugin` and the native build:

```properties
API_KEY_DEBUG="your_debug_key"
API_KEY_RELEASE="your_release_key"
BASE_URL_DEBUG="https://api-debug.example.com/"
BASE_URL_RELEASE="https://api.example.com/"
XOR_MASK="at_least_24_chars_from_A-Za-z0-9._+/=-"
EXPECTED_SIGNATURE_HASH="release_sha256_hex_with_or_without_colons"
NATIVE_RUNTIME_CHECKS_ENABLED=true
CERTIFICATE_PINNING_ENABLED=false
CERTIFICATE_PINS=""

# Required only when a Release task is invoked
STORE_FILE="release.keystore"
STORE_PASSWORD="..."
KEY_ALIAS="..."
KEY_PASSWORD="..."
```

Hard rules enforced by validation:

- Base URLs must be absolute **HTTPS** and end with `/`.
- `XOR_MASK` >= 24 characters, restricted character set.
- `EXPECTED_SIGNATURE_HASH` must be valid SHA-256 hex.
- With pinning enabled, at least **2** `sha256/...` pins.
- Values containing `YOUR_` are rejected as placeholders.
- Environment variables override file values — use this in CI instead of committing the file.

## 3. Validate before building

```bash
./gradlew validateSecrets
./gradlew hardeningReport
```

## 4. Run the CI-equivalent verification locally

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
./gradlew scanApkForSecrets
```

## 5. Add your first feature

```bash
./gradlew scaffoldFeature -PfeatureName=user_profile
./gradlew :feature:user_profile:presentation:compileDebugKotlin
```

Read the task output: it reports whether `settings.gradle.kts` and `app/build.gradle.kts` were actually updated. If either says `not changed`, wire it manually before continuing.

After scaffolding you still need to:

1. Replace the generated placeholder UI/state with the real screen.
2. Register the route as a bottom-bar item if it belongs in the tab bar (multibinding key determines order).
3. Add repository/use-case contracts if the feature needs data.

## First-release checklist

- [ ] `validateSecrets` passes with real values
- [ ] Release keystore configured; `:app:assembleRelease` succeeds
- [ ] `scanApkForSecrets` clean on the release artifact
- [ ] `hardeningReport` reviewed; pinning decision made with a rotation plan
- [ ] Real auth refresh endpoint implemented behind `ITokenRefresher`
- [ ] `minSdk` reviewed against your own audience before the first release
- [ ] Sample features (`detail`, design-system catalog) removed or adapted
- [ ] CI secrets configured as environment variables
- [ ] App name, icon, locales and store metadata updated
- [ ] At least one ViewModel test added for your own feature (the template ships none)

---

[← Previous: 07 - Risks, Gaps and Open Questions](07-risks-and-gaps.md) · [Index](README.md) · *End of series*
