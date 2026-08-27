# Release Readiness

This guide summarizes the minimum release-readiness checks for a ComposeTemplate-based project.

## Build checks

Run:

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Security checks

Before a release, verify:

- `secrets.properties` exists locally but is not committed,
- no placeholder values remain,
- release signing values are configured,
- release base URL uses HTTPS,
- `EXPECTED_SIGNATURE_HASH` is configured,
- `XOR_MASK` meets the minimum strength rule,
- certificate pinning configuration is intentional,
- raw secret values are not visible in built artifacts.

## Artifact scanning

Release builds finalize with artifact scanning where configured. You can also run:

```bash
./gradlew scanApkForSecrets
```

## Hardening report

Run:

```bash
./gradlew hardeningReport
```

Use this to review native secret mode, runtime check configuration, certificate pinning state, and signature hash configuration.

## Release build type

The `release` build type enables:

- minification,
- resource shrinking,
- release signing,
- ProGuard/R8 rules.

## Checklist

- [ ] `validateSecrets` passes.
- [ ] `ktlintCheck` passes.
- [ ] `detekt` passes.
- [ ] `testDebugUnitTest` passes.
- [ ] `assembleDebug` passes.
- [ ] `:app:assembleRelease` passes.
- [ ] release signing is configured.
- [ ] generated APK/AAB artifacts are scanned.
- [ ] logging and sensitive headers are safe for release.
- [ ] certificate pinning decision is documented.

## Repository references

- `app/build.gradle.kts`
- `build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/ValidateSecretsPlugin.kt`
- `docs/security/release-hardening.md`
