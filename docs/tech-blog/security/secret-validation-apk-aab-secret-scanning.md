# Secret Validation and APK/AAB Secret Scanning

Security issues often come from release mistakes, not only from cryptography or backend design.

## Problem

A release build can accidentally ship with placeholder keys, malformed URLs, weak masks, debug metadata, or raw secrets visible inside the final artifact.

## ComposeTemplate approach

ComposeTemplate uses two guardrails:

- `validateSecrets`
- `scanApkForSecrets`

`validateSecrets` checks configuration before the app is built. `scanApkForSecrets` checks generated APK/AAB artifacts for known raw values.

## Takeaway

Validation and artifact scanning are release guardrails. They do not make client-side secrets safe, but they prevent avoidable mistakes.
