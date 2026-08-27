# Validate Secrets

`validateSecrets` catches secret and release-configuration mistakes before they reach a build artifact.

## Problem

Configuration mistakes often reach release builds because local development values are easy to overlook.

Examples:

- missing API keys,
- placeholder values,
- malformed Retrofit base URLs,
- weak obfuscation masks,
- missing release signing values,
- invalid signature hashes,
- invalid certificate pins.

ComposeTemplate makes these failures explicit.

## Command

```bash
./gradlew validateSecrets
```

## Sources

The task reads values from:

- `secrets.properties`,
- supported environment variables.

Environment variables allow CI or hosted build systems to provide values without committing local files.

## Required keys

The task validates required keys such as:

- `API_KEY_DEBUG`,
- `API_KEY_RELEASE`,
- `BASE_URL_DEBUG`,
- `BASE_URL_RELEASE`,
- `XOR_MASK`,
- `EXPECTED_SIGNATURE_HASH`.

Release signing keys are required when release tasks are requested.

## Base URL validation

Base URLs must:

- include `http` or `https`,
- include a host,
- end with `/`,
- use HTTPS for release configuration.

The trailing slash matters because Retrofit requires it for base URLs.

## XOR mask validation

`XOR_MASK` must meet a minimum length and character-safety rule so it can be passed through Gradle/CMake safely.

## Signature hash validation

`EXPECTED_SIGNATURE_HASH` must be a SHA-256 certificate hash, with or without colons.

This supports runtime signature validation in the security layer.

## Certificate pin validation

When certificate pinning is enabled, the task requires at least two pins and validates the expected `sha256/<base64>` format.

## Build lifecycle integration

The validation task is attached to Android `preBuild` tasks for app and library modules. Release artifact tasks also finalize with secret scanning where configured.

## Checklist

- [ ] `secrets.properties` exists locally or env vars provide required values.
- [ ] no placeholder values remain.
- [ ] base URLs are valid and release uses HTTPS.
- [ ] `XOR_MASK` meets strength and safety rules.
- [ ] signature hash is valid.
- [ ] certificate pins are valid when enabled.
- [ ] release signing values exist for release builds.

## Repository references

- `build-logic/convention/ValidateSecretsPlugin.kt`
- `secrets.properties.example`
- `app/build.gradle.kts`
