# Secret Management

Client-side secrets are never truly secret. ComposeTemplate focuses on reducing accidental leakage, increasing extraction cost, and making release configuration mistakes visible before shipping.

## Problem

Mobile apps run on user-controlled devices. Anything packaged into an APK or AAB must be treated as recoverable by a determined attacker.

Common mistakes include:

- committing `secrets.properties`,
- leaving placeholder keys in release builds,
- storing backend master secrets in the client,
- exposing API keys through `BuildConfig`,
- logging tokens or sensitive headers,
- assuming native code makes secrets impossible to extract.

ComposeTemplate does not claim to make client-side secrets impossible to recover. It provides practical hardening and validation layers.

## Design goals

- keep local secrets out of Git,
- validate required configuration before builds,
- support native-backed obfuscation for selected values,
- prevent obvious raw-value leakage into APK/AAB artifacts,
- expose runtime integrity signals,
- document limitations honestly.

## Configuration source

Secrets are configured through `secrets.properties` in the project root or through supported environment variables.

The example file is:

```text
secrets.properties.example
```

Generated apps must create their own local `secrets.properties`; the generator intentionally excludes it.

## Required values

`validateSecrets` checks required values such as:

- `API_KEY_DEBUG`,
- `API_KEY_RELEASE`,
- `BASE_URL_DEBUG`,
- `BASE_URL_RELEASE`,
- `XOR_MASK`,
- `EXPECTED_SIGNATURE_HASH`.

Release signing values are required for release builds.

## Native-backed secret access

`core:secrets` provides `SecretManager`, which can read API key and base URL values through native code when native secrets are enabled.

At startup, `SecretManager` loads the native library when configured:

```kotlin
if (BuildConfig.NATIVE_SECRETS_ENABLED) {
    System.loadLibrary("native-lib")
}
```

If native mode is disabled, values can fall back to generated build config fields.

## Runtime validation

`SecretManager` validates that returned values are usable. For example, base URLs must use HTTPS outside debug builds and must end with `/` because Retrofit requires a trailing slash.

This catches configuration mistakes close to the point of use.

## Validation tasks

ComposeTemplate provides:

| Task | Purpose |
|---|---|
| `validateSecrets` | validates required secret/configuration values |
| `scanApkForSecrets` | scans APK/AAB artifacts for raw configured values |
| `hardeningReport` | prints active client hardening configuration |

## What this protects against

This setup helps against:

- accidental secret commits,
- release builds with placeholders,
- malformed base URLs,
- weak XOR masks,
- missing signature hashes,
- raw values appearing in built artifacts,
- casual static inspection.

## What this does not protect against

This does not protect against:

- backend master secret exposure if such a secret is shipped,
- rooted runtime inspection,
- memory dumping,
- dynamic instrumentation,
- Frida/hooking,
- abused API keys without backend restrictions.

Backend authorization remains the real security boundary.

## Checklist

- [ ] `secrets.properties` is not committed.
- [ ] placeholder values are replaced before release.
- [ ] no backend master secret is shipped in the client.
- [ ] `validateSecrets` runs before build.
- [ ] release signing values are configured.
- [ ] `scanApkForSecrets` runs for release artifacts.
- [ ] tokens and sensitive headers are not logged.
- [ ] backend restrictions exist for client-exposed keys.

## Repository references

- `secrets.properties.example`
- `core/secrets/SecretManager.kt`
- `build-logic/convention/ValidateSecretsPlugin.kt`
- `build-logic/convention/AndroidLibraryNativeConventionPlugin.kt`
