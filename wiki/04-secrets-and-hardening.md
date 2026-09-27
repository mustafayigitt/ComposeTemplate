# 04 - Secrets, Security and Hardening

Secrets and runtime hardening are optional network configuration infrastructure spanning Gradle, CMake, C++, and Kotlin.

## Selection

For `remote` and `offline-first`, secrets default to enabled and can be disabled with the strict Boolean flag:

```bash
./gradlew create-new-app \
  -Pargs='com.example.app,MyApp' \
  -PdataStrategy=remote \
  -PwithSecrets=false
```

`local` and `minimal` always omit the subsystem because its current provider configures `core:network`.

Omission removes secrets/security modules, native/CMake sources, NDK catalog data, native and validation conventions, secret properties/examples, secret-backed signing, CI bootstrap, and generated setup instructions.

## Pipeline

```text
secrets.properties / environment
→ ValidateSecretsPlugin
→ native convention + CMake
→ generated header and obfuscated byte arrays
→ JNI_OnLoad / RegisterNatives
→ SecretManager
→ SecretNetworkConfigProvider
→ NetworkConfigProvider
```

Dynamic JNI registration uses the projected namespace so package rebranding does not break native bindings.

## Guardrails

`validateSecrets` rejects placeholders, invalid HTTPS base URLs, unsafe masks, malformed signature hashes, and insufficient certificate pins. Environment variables override local-file values.

`scanApkForSecrets` searches release artifacts for raw configured values. `hardeningReport` reports the effective native, integrity, and pinning posture.

## Security boundary

- NDK/XOR is obfuscation and extraction-cost hardening, not encryption or secure client-side secret storage.
- Any value shipped to a client should be treated as recoverable.
- Emulator, debugger, and signature checks are bypassable heuristics.
- Certificate pinning is disabled in debug and requires an operational rotation plan in release.
- True secrets and authorization decisions belong server-side.
- Release signing remains the generated product’s responsibility.

## Reporting a vulnerability

Use a private GitHub security advisory rather than a public issue.

---

[← Previous: 03 - Network and Auth Token Flow](03-network-and-auth.md) · [Index](README.md) · [Next: 05 - Generator and Scaffolding Tooling →](05-generator-and-scaffolding.md)
