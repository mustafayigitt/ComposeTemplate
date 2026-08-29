# 04 - Secrets, Security and Hardening

The most mature subsystem in the repository, spanning Gradle, CMake, C++ and Kotlin.

## Pipeline shape

```text
secrets.properties / env vars
        v  ValidateSecretsPlugin (validateSecrets)
        v  AndroidLibraryNative plugin -> CMake defines + secrets_generated.h
        v  native-lib.cpp (XOR-obfuscated byte arrays)
        v  JNI_OnLoad + RegisterNatives
        v  SecretManager (Kotlin) -> NetworkModule.baseUrl / API key
```

## Native layer (`core/secrets/src/main/cpp/native-lib.cpp`)

- Secrets are compiled in as byte arrays (`API_KEY_DEBUG`, `API_KEY_RELEASE`, `BASE_URL_DEBUG`, `BASE_URL_RELEASE`, `EXPECTED_SIGNATURE_HASH`) inside `secrets_generated.h`.
- `decrypt()` reconstructs the XOR mask from **three parts**: `STATIC_MASK` (native constant) + `CM_PART` (injected by CMake) + a runtime mask part passed from Kotlin. No single artifact holds the full mask.
- Runtime integrity checks, active only when `!isDebug && NATIVE_RUNTIME_CHECKS_ENABLED`:
  - `isDebuggerAttached()` — parses `TracerPid:` from `/proc/self/status`.
  - `isEmulator()` — checks `ro.product.model` and `ro.hardware` for `sdk`, `Emulator`, `goldfish`, `ranchu`.
  - `isSignatureValid()` — reads signing certificates via JNI (`SigningInfo` on API 28+, `GET_SIGNATURES` below), SHA-256 hashes each, normalizes (strip `:`, uppercase) and compares to the embedded expected hash.
  - On failure the native function returns the literal string `"UNAUTHORIZED_ACCESS"` rather than crashing.
- **Dynamic JNI registration**: `JNI_OnLoad` + `RegisterNatives` bind `getApiKeyNative` and `getBaseUrlNative` to the class named by `JNI_CLASS_PATH`, injected by CMake from the Gradle `namespace`.

> **Why this matters:** the conventional `Java_com_ytapps_..._method` symbol naming would hardcode the template's package into the exported symbol and break the moment `create-new-app` rebrands the app. Dynamic registration is what makes native secrets survive rebranding — the comment in the source states this explicitly.

## Kotlin layer: `SecretManager`

- Kotlin `object`; loads the native library only when native secrets are enabled, otherwise falls back to `BuildConfig` values.
- `external` declarations: `getApiKeyNative(context, isDebug, runtimeMask)`, `getBaseUrlNative(context, isDebug, runtimeMask)`.
- Rejects blank results and the `"UNAUTHORIZED_ACCESS"` sentinel by throwing a secret-access exception.
- Base URL contract: HTTPS required for release, must end with `/`.
- Requires an explicit `initialize(context)` call at startup — a global object with a nullable context, so initialization order matters.

## Gradle guardrails: `ValidateSecretsPlugin`

Three tasks:

### `validateSecrets` (group `setup`)

- Required keys: `API_KEY_DEBUG`, `API_KEY_RELEASE`, `BASE_URL_DEBUG`, `BASE_URL_RELEASE`, `XOR_MASK`, `EXPECTED_SIGNATURE_HASH`.
- Detects unreplaced placeholders (values containing `YOUR_`).
- `XOR_MASK`: minimum **24** characters and restricted to `A-Za-z0-9._+/=-` — explicitly to keep Gradle/CMake string injection safe.
- Base URLs must be absolute and HTTPS.
- `EXPECTED_SIGNATURE_HASH` must be SHA-256 hex (colons stripped, uppercased).
- If `CERTIFICATE_PINNING_ENABLED=true`, pins must match `^sha256/...` and there must be at least **2** (primary + backup).
- If the invoked task names contain `Release`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` become mandatory (`STORE_FILE` defaults to `release.keystore`).
- **Environment variables override file values** for known secret keys — this is what makes CI work without committing a secrets file.

### `scanApkForSecrets` (group `verification`)

Scans built APK/AAB artifacts for raw secret values, using only secrets long enough to be meaningful needles; fails the build on a hit. This is the check that proves obfuscation actually happened.

### `hardeningReport` (group `verification`)

Prints the effective posture: native secrets on/off, native runtime checks, certificate pinning state, pin count, whether a release signature hash is configured.

## `core:security`

`DeviceIntegrityManager` plus a small policy model: `SecurityPolicy`, `SecurityFinding`, `SecurityAction`, `SecurityReport` — findings are surfaced as data with an associated action rather than hard-coded reactions.

## Honest limitations (stated by the code itself)

- XOR + NDK is **obfuscation, not encryption**. A determined attacker with the APK can recover secrets; this raises cost, it does not prevent extraction.
- Emulator and debugger checks are heuristics and are bypassable.
- Certificate pinning is disabled in debug, so pin mistakes only surface in release builds.
- Any true secret should still live server-side.

## Reporting a vulnerability

Security issues in this template should be reported privately to the maintainer via GitHub (open a private security advisory on the repository) rather than as a public issue.

---

[← Previous: 03 - Network and Auth Token Flow](03-network-and-auth.md) · [Index](README.md) · [Next: 05 - Generator and Scaffolding Tooling →](05-generator-and-scaffolding.md)
