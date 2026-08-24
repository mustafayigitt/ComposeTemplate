# Certificate Pinning: Security vs Operational Risk

Certificate pinning can make man-in-the-middle attacks harder, but it also adds operational risk.

## Problem

HTTPS relies on the platform trust store. On compromised or managed devices, trust can be manipulated. Pinning narrows trust by requiring the server certificate chain to match expected public keys.

## Best practices

- Pin SPKI hashes, not entire leaf certificates.
- Keep at least two pins: current and backup.
- Avoid overly broad wildcard host scopes.
- Plan certificate rotation before enabling pinning.

## Takeaway

Certificate pinning is useful when operated carefully. Without rotation planning, it can become an availability risk.
