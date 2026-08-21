# Contribution Guide

A contribution should protect both the current app and the template generator behavior.

## Local verification

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug
```

For release/security-impacting changes:

```bash
./gradlew validateSecrets :app:assembleRelease scanApkForSecrets
```

For generator-impacting changes:

```bash
./gradlew scaffoldFeature -PfeatureName=test_feature
./gradlew create-new-app -Pargs='com.example.generated,GeneratedApp' -q --console=plain
```

## Rules

- Do not add business logic to app module.
- Keep domain modules away from Android implementation details.
- Do not leak DTOs into UI.
- Do not commit real secrets.
- Do not log tokens or sensitive headers.
