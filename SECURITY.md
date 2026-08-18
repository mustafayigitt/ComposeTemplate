# Security Policy

ComposeTemplate ships several security-relevant building blocks out of the box — NDK-backed secret
obfuscation (`core:secrets`), runtime integrity checks (`core:security`), build-time secret
validation (`validateSecrets`), and optional certificate pinning. Because these primitives are meant
to be copied into real, production apps via `create-new-app`, vulnerabilities here have outsized
impact and we take reports seriously.

## Reporting a Vulnerability

Please **do not** open a public GitHub issue for security vulnerabilities.

Instead, report privately via [GitHub Security Advisories](https://github.com/mustafayigitt/ComposeTemplate/security/advisories/new)
for this repository. If that is not available to you, open a normal issue asking for a private
contact channel and avoid including exploit details.

When reporting, please include:

- A description of the vulnerability and its potential impact.
- Steps to reproduce, or a minimal proof of concept.
- The affected module(s) or file(s) (e.g. `core/secrets`, `core/security`, `ValidateSecretsPlugin.kt`).
- Any suggested remediation, if you have one.

## Response Expectations

- **Acknowledgement**: within 5 business days of the report.
- **Triage**: we'll confirm whether it's a genuine issue and assess severity within 10 business days.
- **Fix & disclosure**: timeline depends on severity and complexity; we'll keep you updated
  throughout and credit you in the release notes (unless you prefer to stay anonymous).

## Scope

In scope:

- The convention plugins in `build-logic/` (especially `ValidateSecretsPlugin`, `CreateNewAppPlugin`).
- The native secret handling in `core/secrets` (`cpp/native-lib.cpp`, `SecretManager.kt`).
- The runtime integrity checks in `core/security`.
- Network hardening (certificate pinning, cleartext traffic policy) in `core/network`.

Out of scope:

- Vulnerabilities in third-party dependencies (Retrofit, Hilt, Room, etc.) — please report those
  upstream. If a dependency issue affects this template specifically (e.g. we're pinned to a known
  vulnerable version), that's welcome here too.
- Issues that require a rooted/jailbroken device or physical access to an already-compromised
  device, unless they demonstrate a bypass of a control this template specifically claims to
  provide (e.g. root detection, signature verification).

See [`SECRET_MANAGEMENT.md`](SECRET_MANAGEMENT.md) for the full secret-handling threat model and
the release checklist apps generated from this template should follow.
