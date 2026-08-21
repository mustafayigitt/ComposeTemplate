# Certificate Pinning

Certificate pinning makes MITM attacks harder but introduces operational risk.

## Best practices

- Use at least two pins: current and backup.
- Prefer SPKI pins.
- Be careful with wildcard host scope.
- Plan certificate rotation before enabling pinning.
- Do not treat client-only remote config as a security boundary.

## Checklist

- [ ] Current and backup pins exist.
- [ ] SPKI pins are used.
- [ ] A rotation plan exists.
- [ ] Backend authorization remains the decision point.
