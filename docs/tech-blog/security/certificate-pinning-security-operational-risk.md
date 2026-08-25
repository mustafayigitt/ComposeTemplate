# Certificate Pinning: Security vs Operational Risk

## Who this article is for

This article is for Android developers responsible for network security, OkHttp configuration, or mobile release operations.

## What you will learn

- What certificate pinning protects against
- Why SPKI pins are preferred
- Why pinning can break production apps
- How to plan rotation and recovery

## The problem

HTTPS trusts certificate authorities in the platform trust store. In some environments, that trust can be modified or abused. Certificate pinning narrows trust by requiring the server certificate chain to match known public key hashes.

## What pinning protects against

Pinning can make some man-in-the-middle attacks harder, especially when a malicious or user-installed CA is involved.

It does not protect against compromised app code, runtime hooks, stolen tokens, backend authorization flaws, or malicious servers that legitimately own the pinned key.

## SPKI pinning

Pin the Subject Public Key Info hash rather than an entire certificate. Certificates rotate more often than public keys. SPKI pinning gives better operational flexibility.

## Backup pins

Always keep at least one backup pin. Without a backup, certificate rotation can brick network access for existing app versions.

## ComposeTemplate approach

ComposeTemplate treats certificate pinning as a configurable hardening layer. That is important because pinning should not be enabled casually. It needs release planning.

## Operational risks

Pinning failure is an availability issue. If pins are wrong, the app cannot connect. Mobile clients are slow to update, so bad pins can remain in the wild.

## Common mistakes

- Pinning without backup pins.
- Pinning leaf certificates instead of SPKI.
- Forgetting staging vs production hosts.
- Using broad wildcard assumptions.
- No emergency rotation plan.

## Production checklist

- [ ] SPKI pins are used.
- [ ] Current and backup pins exist.
- [ ] Rotation process is documented.
- [ ] Staging and production hosts are separated.
- [ ] Monitoring exists for pinning failures.
- [ ] Emergency release plan exists.

## Summary

Certificate pinning is a useful hardening tool, but it is also operationally dangerous. ComposeTemplate frames it as an optional security layer that requires rotation discipline.