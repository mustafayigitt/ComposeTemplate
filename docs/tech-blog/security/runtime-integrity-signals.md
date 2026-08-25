# Runtime Integrity Signals on Android

## Who this article is for

This article is for Android developers adding root, debugger, emulator, hook, installer, or signature checks to an app.

## What you will learn

- Why client-side integrity checks are bypassable
- How to think about checks as risk signals
- What kinds of signals are useful
- Operational and privacy concerns

## The problem

Apps often want to know whether they are running in a risky environment. Examples include rooted devices, emulators, attached debuggers, hook frameworks, repackaged APKs, or unknown installers.

The mistake is treating the client check as a final security decision.

A motivated attacker can patch client code, hook functions, fake results, or modify control flow.

## Better mental model

Runtime integrity should be modeled as signals:

```text
collect signals -> evaluate risk -> decide response
```

The response might be local UX changes, reduced trust, server-side risk scoring, or additional verification.

## ComposeTemplate approach

ComposeTemplate’s `core:security` exposes runtime security signals such as:

- debugger attachment,
- emulator heuristics,
- root indicators,
- hook indicators,
- installer source,
- signature mismatch,
- tamper indicators.

The important point is not that any single signal is perfect. The value is in combining weak signals into a risk picture.

## Red-oracle risk

Do not give attackers precise failure messages like `FRIDA_DETECTED`. That tells them exactly what to patch.

Prefer generic responses and server-side correlation where appropriate.

## Privacy considerations

Integrity signals can become fingerprinting data. Collect the minimum needed, document why it is collected, and define retention policy.

## Common mistakes

- Blocking only on one weak signal.
- Exposing detailed detection reasons.
- Treating root detection as complete protection.
- Collecting excessive device data.

## Production checklist

- [ ] Signals are documented as bypassable.
- [ ] No single signal is treated as absolute truth.
- [ ] User-facing errors do not reveal detection internals.
- [ ] Backend risk scoring is considered for sensitive flows.
- [ ] Privacy and retention are documented.

## Summary

Runtime integrity checks are useful when treated honestly. ComposeTemplate models them as risk signals, not unbreakable client-side security.