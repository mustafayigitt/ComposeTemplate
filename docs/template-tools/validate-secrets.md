# Validate Secrets

`validateSecrets` catches secret/configuration mistakes before release.

## Command

```bash
./gradlew validateSecrets
```

## Validates

- Required keys exist
- Placeholder values are not used
- URL values are valid
- XOR mask is long/strong enough
- Signature hash format is valid
- Pinning values are valid when enabled

## Security note

`validateSecrets` does not make client-side secrets truly secure. It prevents configuration mistakes and accidental leakage.
