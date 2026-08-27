# Release Hardening

Release hardening prevents debug-time behavior, unsafe configuration, and accidental secret leakage from reaching production builds.

## Problem

A debug build can tolerate local configuration, verbose logging, relaxed security, and developer conveniences. A release build cannot.

Release hardening makes those expectations explicit and reviewable.

## Release build type

The app release build enables:

```kotlin
release {
    isMinifyEnabled = true
    isShrinkResources = true
    signingConfig = signingConfigs.getByName("release")
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
    )
}
```

## Signing

Release signing values are loaded from secrets or local properties. `validateSecrets` checks required release signing values when release tasks are requested.

Required release signing configuration includes:

- store file,
- key alias,
- key password,
- store password.

## Secret validation

Before release, run:

```bash
./gradlew validateSecrets
```

This catches missing values, placeholders, malformed URLs, weak masks, invalid signature hashes, and invalid certificate pinning configuration.

## Artifact scanning

Release builds finalize with APK/AAB secret scanning where configured. You can run it directly:

```bash
./gradlew scanApkForSecrets
```

The scanner checks generated artifacts for raw configured values that should not appear plainly.

## Logging policy

Release builds should avoid body logging and redact sensitive headers such as authorization, cookies, API keys, and tokens.

Debug-friendly network logs must not become release behavior.

## Benchmark build type

The benchmark build type is initialized from release and uses release-like optimization settings. This gives performance tests more realistic characteristics than debug builds.

## Checklist

- [ ] release minification is enabled.
- [ ] resource shrinking is enabled.
- [ ] release signing is configured.
- [ ] `validateSecrets` passes.
- [ ] `scanApkForSecrets` runs on generated artifacts.
- [ ] release base URL uses HTTPS.
- [ ] logging is safe for release.
- [ ] certificate pinning decision is intentional.
- [ ] benchmark variant remains release-like.

## Repository references

- `app/build.gradle.kts`
- `build-logic/convention/ValidateSecretsPlugin.kt`
- `secrets.properties.example`
- `.github/workflows/ci.yml`
