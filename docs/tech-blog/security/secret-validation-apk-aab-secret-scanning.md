# Secret Validation and APK/AAB Secret Scanning

## Who this article is for

This article is for Android developers, release engineers, and DevSecOps teams who want guardrails against accidental secret leakage.

## What you will learn

- Why release mistakes are common
- What secret validation should catch
- What artifact scanning can and cannot prove
- How ComposeTemplate uses validation and scanning as release guardrails

## The problem

Many mobile security issues come from configuration mistakes:

- placeholder API keys,
- debug endpoints,
- malformed URLs,
- weak masks,
- wrong signature hashes,
- raw values visible in APK/AAB output.

These mistakes are preventable with build-time checks.

## Validation vs scanning

Validation happens before or during build. It checks whether configuration values are present, well-formed, and not obvious placeholders.

Artifact scanning happens after build. It inspects generated outputs for known raw values or suspicious strings.

Both are useful. Neither proves the app is secure.

## ComposeTemplate approach

ComposeTemplate provides tasks such as:

- `validateSecrets`
- `scanApkForSecrets`
- `hardeningReport`

The goal is to fail early when release configuration looks unsafe.

## What validation catches

- Missing required properties
- Placeholder values
- Malformed base URLs
- Weak XOR masks
- Invalid signature hash format
- Inconsistent certificate pinning configuration

## What scanning catches

- Raw API keys
- Base URLs
- Internal endpoints
- Placeholder strings
- Debug metadata
- Known secret values in final artifacts

## Limitations

A scan only finds what it knows to look for. It cannot prove that no sensitive value exists. It also does not protect runtime memory or backend authorization.

## Production checklist

- [ ] Release builds run secret validation.
- [ ] APK/AAB artifacts are scanned.
- [ ] Placeholder values fail the build.
- [ ] Reports are reviewable in CI.
- [ ] Real backend secrets are never stored in the client.

## Summary

Secret validation and artifact scanning are practical release guardrails. ComposeTemplate uses them to catch avoidable mistakes before they reach production.