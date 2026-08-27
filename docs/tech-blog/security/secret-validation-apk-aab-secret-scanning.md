# Secret Validation and APK/AAB Secret Scanning

## Who this article is for

This article is for Android developers who want release guardrails around client-side configuration and secret handling.

## What you will learn

- why client-side secret validation matters
- what `validateSecrets` checks
- why artifact scanning is different from source validation
- what these tools can and cannot guarantee

## The problem

Mobile releases can fail because of configuration mistakes: placeholder keys, invalid URLs, missing release signing, weak masks, or raw values packaged into artifacts.

These mistakes are preventable if the build fails early.

## Why this matters for Android projects

A mobile release is hard to roll back. Once an APK/AAB is shipped, bad configuration can remain on user devices for a long time.

Guardrails should run before release artifacts are distributed.

## ComposeTemplate's approach

ComposeTemplate provides three related tasks:

| Task | Purpose |
|---|---|
| `validateSecrets` | validates configuration before build |
| `scanApkForSecrets` | scans generated artifacts for raw values |
| `hardeningReport` | summarizes hardening configuration |

## Validation walkthrough

`validateSecrets` checks required values such as API keys, base URLs, XOR mask, and expected signature hash.

It also validates:

- placeholder values
- base URL shape and trailing slash
- HTTPS for release base URLs
- XOR mask length and safe characters
- SHA-256 signature hash format
- certificate pin format when pinning is enabled
- release signing keys for release builds

## Artifact scanning

`scanApkForSecrets` looks at built APK/AAB outputs and searches for configured raw values. This catches cases where source configuration looked valid but the final artifact still exposes a value plainly.

## Limitations

A clean scan does not prove a client-side value is unrecoverable. It only reduces obvious leakage. Native obfuscation, validation, and scanning increase cost; backend authorization remains essential.

## Design trade-offs

Strict validation can block local builds until configuration is correct. This is intentional for a template focused on release safety.

## Production checklist

- [ ] `validateSecrets` runs before builds
- [ ] release signing is validated for release tasks
- [ ] base URLs are valid and release uses HTTPS
- [ ] certificate pins are validated when enabled
- [ ] release artifacts are scanned
- [ ] scan failures block release
- [ ] backend secrets are never shipped in the client

## Takeaways

- Validation prevents known configuration mistakes.
- Artifact scanning checks the output, not just source files.
- These tools are guardrails, not absolute secrecy guarantees.
