# 04 - Secrets, Security and Hardening

Secrets and runtime hardening are optional network configuration infrastructure spanning Gradle, CMake, C++, and Kotlin.

## Selection

For `remote` and `offline-first`, secrets default to enabled and may be omitted strictly with:

```bash
./gradlew create-new-app \
  -Pargs='com.example.app,MyApp' \
  -PdataStrategy=remote \
  -PwithSecrets=false
```

`local` and `minimal` always omit this capability because the current implementation contributes a secret-backed `NetworkConfigProvider` and therefore depends on `core:network`.

Omission removes:

- `core:secrets` and the dependent `core:security`
- native/CMake sources and the NDK catalog entry
- native and validation convention plugins
- secret Gradle properties and example files
- secret-backed signing setup and CI bootstrap
- setup instructions from the generated consumer documentation

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

Dynamic JNI registration uses the Gradle namespace, so package rebranding does not break native bindings.

## Guardrails

`validateSecrets` rejects placeholders, invalid HTTPS base URLs, short/unsafe masks, malformed signature hashes, and insufficient certificate pins. Environment variables override local-file values.

`scanApkForSecrets` scans release artifacts for raw values. `hardeningReport` prints the effective native, integrity, and pinning posture.

## Limitations

- NDK/XOR raises extraction cost; it is not encryption.
- Emulator, debugger, and signature checks are bypassable heuristics.
- Certificate pinning is disabled in debug.
- True secrets belong server-side.

## Reporting a vulnerability

Use a private GitHub security advisory rather than a public issue.

---

[← Previous: 03 - Network and Auth Token Flow](03-network-and-auth.md) · [Index](README.md) · [Next: 05 - Generator and Scaffolding Tooling →](05-generator-and-scaffolding.md)
