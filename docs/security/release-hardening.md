# Release Hardening

Release hardening prevents debug-time behavior and unsafe configuration from reaching production builds.

## Checks

- Release minification enabled
- Resource shrinking enabled
- Debuggable/profileable flags do not leak into release
- Body logging disabled in release
- Sensitive headers redacted
- `validateSecrets` passes
- `scanApkForSecrets` runs
- Release signing configured

## Example

```kotlin
release {
    isMinifyEnabled = true
    isShrinkResources = true
}
```
