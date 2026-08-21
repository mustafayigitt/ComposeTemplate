# Runtime Security Signals

Runtime security should be treated as a risk-signal layer, not as a final decision layer.

## Signals

`core:security` can surface signals such as debugger attachment, emulator heuristics, root artifacts, hook indicators, installer source, signature mismatch, and runtime tamper signals.

## Signal, not decision

Avoid:

```text
if rooted -> block forever
```

Prefer:

```text
collect signals -> calculate risk -> backend decision / risk-based UX
```

## Checklist

- [ ] Runtime checks are not the final decision.
- [ ] Backend risk modeling is considered.
- [ ] Privacy and compliance impact is reviewed.
