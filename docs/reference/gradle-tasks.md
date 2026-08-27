# Gradle Tasks Reference

This page lists important ComposeTemplate Gradle tasks.

| Task | Purpose |
|---|---|
| `create-new-app` | Generates a new app from the template |
| `scaffoldFeature` | Generates a feature module set |
| `validateSecrets` | Validates required secret/configuration values |
| `scanApkForSecrets` | Scans built APK/AAB artifacts for raw secret values |
| `hardeningReport` | Prints active client hardening configuration |
| `ktlintCheck` | Runs Kotlin style checks |
| `detekt` | Runs static analysis |
| `testDebugUnitTest` | Runs debug unit tests |
| `assembleDebug` | Builds debug APK |
| `:app:assembleRelease` | Builds release APK |
| `:baselineprofile:connectedBenchmarkAndroidTest` | Generates Baseline Profile rules |
| `:benchmark:connectedBenchmarkAndroidTest` | Runs Macrobenchmark tests |

## Common verification set

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Template verification

```bash
./gradlew scaffoldFeature -PfeatureName=ci_feature
./gradlew :feature:ci_feature:presentation:compileDebugKotlin
./gradlew create-new-app -Pargs='com.example.generated,GeneratedApp' -q --console=plain
```

## Repository references

- `.github/workflows/ci.yml`
- `build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention`
