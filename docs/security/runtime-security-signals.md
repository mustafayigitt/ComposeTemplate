# Runtime Security Signals

Runtime security checks in ComposeTemplate are treated as risk signals, not absolute security decisions.

## Problem

Client-side runtime checks are useful, but they are bypassable. Root detection, emulator detection, signature checks, and hook detection can all be evaded by a capable attacker.

The mistake is treating these checks as final proof that a device is safe or unsafe.

ComposeTemplate uses them as signals that can influence risk-based behavior.

## Implementation model

`core:security` provides runtime integrity evaluation through `DeviceIntegrityManager` and `SecurityReport`.

The manager evaluates a set of findings and returns an action:

```text
findings -> SecurityPolicy -> SecurityAction
```

## Signals

ComposeTemplate can surface findings such as:

- package name mismatch,
- signing certificate mismatch,
- unexpected installer,
- emulator heuristics,
- debugger attachment,
- root indicators,
- hooking indicators.

## Security report

A report contains:

- `action`,
- `findings`,
- `isBlocked` convenience state.

The action can represent allow, warn, or block behavior depending on policy.

## Signal, not decision

Avoid treating one client-side signal as a final decision:

```text
if rooted -> permanently block the account
```

Prefer a risk model:

```text
collect signals -> calculate risk -> apply local UX or backend policy
```

## Privacy and UX considerations

Runtime integrity checks can affect legitimate users, especially on emulators, rooted development devices, enterprise-managed devices, and non-standard app stores.

Before blocking, consider:

- false positives,
- accessibility and developer workflows,
- regional installer differences,
- compliance requirements,
- backend-side risk scoring.

## Checklist

- [ ] runtime signals are not treated as perfect proof.
- [ ] policy distinguishes warn vs block behavior.
- [ ] backend risk modeling is considered for sensitive actions.
- [ ] false-positive impact is reviewed.
- [ ] integrity findings are not logged with sensitive user data.
- [ ] generated apps adapt policy to their threat model.

## Repository references

- `core/security/DeviceIntegrityManager.kt`
- `core/security/SecurityReport.kt`
- `core/security/SecurityPolicy.kt`
- `core/security/SecurityFinding.kt`
