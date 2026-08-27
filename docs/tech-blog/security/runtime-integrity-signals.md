# Runtime Integrity Signals on Android

## Who this article is for

This article is for Android and security-minded developers designing client-side risk signals.

## What you will learn

- why runtime checks are signals, not proof
- what kinds of findings ComposeTemplate surfaces
- how policy can translate findings into actions
- what limitations to document honestly

## The problem

Root checks, emulator checks, debugger checks, and hook detection are useful but bypassable. Treating them as perfect security boundaries creates false confidence and can harm legitimate users.

## Why this matters for Android projects

Mobile apps run in untrusted environments. Runtime integrity signals can help detect risky contexts, but final authorization decisions should remain server-side for sensitive operations.

## ComposeTemplate's approach

ComposeTemplate uses `DeviceIntegrityManager` to evaluate findings and return a `SecurityReport`.

Findings can include package mismatch, signature mismatch, unexpected installer, emulator, debugger attachment, root signals, and hooking signals.

A policy decides whether findings result in allow, warn, or block behavior.

## Signal model

Prefer:

```text
collect findings -> evaluate policy -> adjust UX or backend risk score
```

Avoid:

```text
one local finding -> permanent account decision
```

## Design trade-offs

Strict runtime blocking can create false positives. Warning-only behavior is safer for UX but weaker as a deterrent. Each generated app should choose a policy based on its threat model.

## Production checklist

- [ ] findings are treated as risk signals
- [ ] policy is appropriate for the app
- [ ] backend remains final authority for sensitive actions
- [ ] false positives are considered
- [ ] logs do not expose sensitive context

## Takeaways

- Runtime checks raise visibility; they do not prove safety.
- Policy should separate detection from decision.
- Honest limitations are part of good security documentation.
