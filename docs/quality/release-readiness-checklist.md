# Release Readiness Checklist

## Build

- [ ] `assembleDebug` passes.
- [ ] `:app:assembleRelease` passes.
- [ ] Release signing is configured.
- [ ] `isMinifyEnabled = true`.
- [ ] `isShrinkResources = true`.

## Secrets

- [ ] `secrets.properties` exists and is not committed.
- [ ] `validateSecrets` passes.
- [ ] No placeholder secrets remain.
- [ ] `scanApkForSecrets` runs.

## Network

- [ ] Cleartext traffic is disabled.
- [ ] Sensitive headers are redacted.
- [ ] Body logging is disabled in release.
- [ ] Certificate-pinning decision is intentional.

## Minimum command set

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
./gradlew scanApkForSecrets
```
