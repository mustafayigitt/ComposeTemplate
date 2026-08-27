# Release Readiness Checklist

Use this checklist before preparing a release build from ComposeTemplate or a generated app.

## Build

- [ ] `assembleDebug` passes.
- [ ] `:app:assembleRelease` passes.
- [ ] release signing is configured.
- [ ] `isMinifyEnabled = true` for release.
- [ ] `isShrinkResources = true` for release.
- [ ] ProGuard/R8 rules are reviewed.

## Secrets and configuration

- [ ] `secrets.properties` exists locally or CI provides equivalent environment variables.
- [ ] `secrets.properties` is not committed.
- [ ] `validateSecrets` passes.
- [ ] no placeholder values remain.
- [ ] release base URL uses HTTPS.
- [ ] `XOR_MASK` meets validation rules.
- [ ] `EXPECTED_SIGNATURE_HASH` is configured.
- [ ] release signing values are configured.

## Artifact safety

- [ ] `scanApkForSecrets` runs after release artifact generation.
- [ ] no raw configured secret values are found in APK/AAB output.
- [ ] debug files, logs, local properties, and generated artifacts are not committed.

## Network and security

- [ ] cleartext traffic policy is intentional.
- [ ] sensitive headers are redacted.
- [ ] body logging is disabled in release.
- [ ] certificate pinning decision is documented.
- [ ] runtime integrity policy is appropriate for the app's threat model.
- [ ] backend authorization remains the final security boundary.

## Quality gates

- [ ] `ktlintCheck` passes.
- [ ] `detekt` passes.
- [ ] `testDebugUnitTest` passes.
- [ ] generated-feature smoke checks pass when generator code changes.
- [ ] generated-app smoke checks pass when template identity or generator behavior changes.

## Performance

- [ ] Baseline Profile is up to date for startup flow.
- [ ] Macrobenchmark results are reviewed for startup-sensitive changes.
- [ ] benchmark build remains release-like.

## Minimum command set

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
./gradlew scanApkForSecrets
```

## Related documentation

- [Release Readiness Guide](../guides/release-readiness.md)
- [Secret Management](../security/secret-management.md)
- [CI Pipeline](../build-system/ci-pipeline.md)
