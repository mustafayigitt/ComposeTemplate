# APK/AAB Secret Scanning

APK/AAB secret scanning checks whether raw secrets or masks are visible inside build artifacts.

## Command

```bash
./gradlew :app:assembleRelease
./gradlew scanApkForSecrets
```

## Scans for

- API keys
- Base URLs
- XOR masks
- Internal endpoints
- Placeholder secrets
- Native symbol names
- Debug metadata

## Limitation

Artifact scanning is not a complete security guarantee. It detects known leakage patterns.
