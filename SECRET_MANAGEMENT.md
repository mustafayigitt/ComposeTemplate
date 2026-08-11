# Android Secret Management

This template provides a layered secret management approach to increase the extraction cost of sensitive config values from Android clients in scenarios without a dedicated backend.

Important limitation: any value placed inside a public mobile application can be extracted by a sufficiently motivated attacker. This architecture does not make secrets impossible to extract; it increases the cost of reverse engineering and decompilation through native obfuscation, runtime integrity checks, and MITM protections.

## Layers

1. Local/CI secret loading
   - During local development, `secrets.properties` is used.
   - For CI, the same key names can be provided as environment variables.
   - `secrets.properties` is kept outside of git.

2. Build-time validation
   - `validateSecrets` runs before Android `preBuild` flows.
   - Missing values, empty secrets, `YOUR_` placeholders, short `XOR_MASK`, invalid SHA-256 signature hashes, and Retrofit base URLs without trailing slashes will stop the build.
   - Missing release signing keys cause a failure rather than a warning.

3. Native secret obfuscation
   - When `composetemplate.useNativeSecrets=true`, API key and base URL values are not written to BuildConfig as plain text.
   - Values are XOR-obfuscated at build time and written to a generated C++ header as byte arrays.
   - The XOR mask is split across a static header, CMake define, and Kotlin runtime piece.
   - The native layer can check app signature, emulator, and debugger signals in release builds.

4. Runtime integrity checks
   - The `core:security` module collects signals for app signature, package name, installer, emulator, debugger, root, and hooking.
   - In debug builds, findings produce warnings.
   - In release builds with `NATIVE_RUNTIME_CHECKS_ENABLED=true`, startup is blocked on findings.

5. Network/MITM hardening
   - The main network security config disables cleartext and trusts only system CAs.
   - Debug resources provide overrides for localhost/10.0.2.2 cleartext and user CAs.
   - When `CERTIFICATE_PINNING_ENABLED=true`, the release OkHttp client expects primary + backup `sha256/...` pins.

6. Artifact scan
   - `scanApkForSecrets` searches APK/AAB artifacts for raw `API_KEY_*` and `BASE_URL_*` values.
   - Runs automatically after release `assembleRelease` and `bundleRelease`.
   - `hardeningReport` prints the active secret management/hardening configuration.

## secrets.properties

After generating a new app, start from the example file in the root directory:

```bash
cp secrets.properties.example secrets.properties
```

```properties
API_KEY_DEBUG="debug_key"
API_KEY_RELEASE="release_key"

BASE_URL_DEBUG="https://api-debug.example.com/"
BASE_URL_RELEASE="https://api.example.com/"

XOR_MASK="at_least_24_chars_mask_value"
EXPECTED_SIGNATURE_HASH="AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899"
NATIVE_RUNTIME_CHECKS_ENABLED=true

CERTIFICATE_PINNING_ENABLED=false
CERTIFICATE_PINS="sha256/primaryBase64PinHereAAAAAAAAAAAAAAA=,sha256/backupBase64PinHereBBBBBBBBBBBBBBB="

STORE_FILE="release.keystore"
KEY_ALIAS="release_key_alias"
KEY_PASSWORD="release_key_password"
STORE_PASSWORD="release_store_password"
```

`EXPECTED_SIGNATURE_HASH` can be provided with or without colons; the build and native layer normalize it. If using Google Play App Signing, use the App signing key certificate SHA-256 hash from the Play Console for release.

## Pinning

Use the OkHttp pin format:

```properties
CERTIFICATE_PINNING_ENABLED=true
CERTIFICATE_PINS="sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=,sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
```

Provide at least two pins: the current pin and a backup/rotation pin. Pinning is automatically bypassed in debug builds and enforced in release builds.

## Commands

```bash
./gradlew validateSecrets
./gradlew hardeningReport
./gradlew :core:secrets:assembleDebug
./gradlew :app:compileDebugKotlin
./gradlew :app:assembleRelease
```

## Release Checklist

- `./gradlew validateSecrets`
- `./gradlew hardeningReport`
- `EXPECTED_SIGNATURE_HASH` matches the release signing cert
- `BASE_URL_RELEASE` uses HTTPS and ends with a trailing slash
- If `CERTIFICATE_PINNING_ENABLED=true`, at least two pins are present
- Release signing keys are provided via env vars or `secrets.properties`
- After `./gradlew :app:assembleRelease`, `scanApkForSecrets` passes cleanly

## Threat Model

This architecture aims to mitigate the following risks:

- Secret values appearing as plain text via JADX/decompile
- Raw keys appearing in APK artifacts via `strings`
- Artifact generation with incorrect base URLs or placeholder values
- Release MITM attempts via user CA
- Simple re-signed clone apps accessing release secrets
- Runtime analysis signals (debugger, emulator, root, hooking)

This architecture does NOT guarantee:

- Permanent secrecy of client-side values
- Prevention of in-memory value extraction at runtime
- Unbypassable Frida/Xposed/root checks
- Unpatchable native/Kotlin integrity checks
- Replacement for backend authorization, token expiration, or attestation

Truly high-value secrets should be protected by backend, token exchange, expiration, Firebase/Supabase rules, Play Integrity/App Attest, and server-side controls.
