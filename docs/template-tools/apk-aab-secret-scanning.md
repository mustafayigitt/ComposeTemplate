# APK/AAB Secret Scanning

`scanApkForSecrets` checks generated APK/AAB artifacts for raw configured secret values.

## Problem

Even when configuration is valid, a release artifact can accidentally contain raw values in an easily extractable form.

Artifact scanning is a final guardrail that looks at the built output rather than only source files.

## Command

```bash
./gradlew scanApkForSecrets
```

Release build tasks can also finalize with this scan where configured.

## What it scans

The task scans APK/AAB files under app build outputs and checks for configured values that are long enough to be meaningful findings.

It focuses on raw values such as API keys and base URLs.

## What a finding means

A finding means a configured raw value appears directly in the built artifact. That should be investigated before release.

Possible causes include:

- value exposed through generated constants,
- raw string retained in resources,
- debug configuration included in release,
- native obfuscation disabled intentionally or accidentally,
- logging or config files packaged into the app.

## Limitations

This is not a full reverse-engineering analysis. It does not prove that a secret is unrecoverable. It only checks for obvious raw-value leakage.

A clean scan is a guardrail, not a guarantee.

## Checklist

- [ ] release artifact exists before scanning.
- [ ] scan runs after release assemble/bundle.
- [ ] findings fail the build.
- [ ] raw values are investigated before release.
- [ ] scan results are treated as a guardrail, not a proof of secrecy.

## Repository references

- `build-logic/convention/ValidateSecretsPlugin.kt`
- `app/build/outputs`
- `.github/workflows/ci.yml`
