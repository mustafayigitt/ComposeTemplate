# Certificate Pinning: Security vs Operational Risk

## Who this article is for

This article is for Android developers deciding whether to enable certificate pinning.

## What you will learn

- what certificate pinning protects against
- why SPKI pins are preferred
- why backup pins are mandatory
- how pinning can break production apps

## The problem

Certificate pinning can make certain interception attacks harder, but it also creates a failure mode where valid production networking breaks after certificate or infrastructure changes.

Mobile clients update slowly, so a bad pin can stay deployed.

## Why this matters for Android projects

Network security is both a security problem and an operations problem. Strong client-side restrictions can backfire if there is no rotation plan.

## ComposeTemplate's approach

ComposeTemplate exposes pinning configuration through secret/config values and validates pin format when enabled.

`validateSecrets` requires at least primary and backup pins when certificate pinning is enabled.

## SPKI and backup pins

Prefer SPKI public key pins. Always configure at least two pins:

- active pin
- backup pin

The backup pin is your recovery path.

## Design trade-offs

Pinning can reduce some MITM risk, but it increases operational responsibility. If your backend, CDN, or certificate management changes, app networking can fail.

## Production checklist

- [ ] pinning decision is intentional
- [ ] SPKI pins are used
- [ ] primary and backup pins exist
- [ ] rotation plan is documented
- [ ] emergency recovery path exists
- [ ] backend authorization remains the real security boundary

## Takeaways

- Pinning is powerful but risky.
- Backup pins are not optional.
- Pinning does not replace backend authorization.
