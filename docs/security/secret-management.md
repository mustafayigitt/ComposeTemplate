# Secret Management

Client-side secrets are never truly secret. ComposeTemplate focuses on increasing extraction cost and preventing accidental leakage, not replacing backend security.

## Approach

- `secrets.properties` for local configuration
- `validateSecrets` for build-time validation
- `core:secrets` for NDK/CMake-backed obfuscation
- `scanApkForSecrets` for artifact scanning
- `hardeningReport` for release-readiness feedback
- `core:security` for runtime integrity signals

## Build-time secrets are not real secrets

Anything shipped inside an APK/AAB must be considered extractable. Native obfuscation, XOR masks, and string splitting only increase reverse-engineering cost.

## Checklist

- [ ] No real server secret is shipped in the APK/AAB.
- [ ] `secrets.properties` is not committed.
- [ ] `validateSecrets` runs before release.
- [ ] Tokens are not logged.
- [ ] Client hardening is not treated as backend security.
