# Certificate Pinning: Security vs Operational Risk

## Who this article is for

This article is for Android developers configuring network security or operating mobile releases.

## What you will learn

- What certificate pinning protects against
- Why SPKI pinning is preferred
- Why backup pins matter
- How pinning can break production apps
- How ComposeTemplate frames pinning as optional hardening

## The problem

HTTPS trusts certificate authorities in the platform trust store. In some environments, that trust can be modified. Certificate pinning narrows trust by requiring the server chain to match expected public key hashes.

## What pinning does and does not solve

Pinning can make certain man-in-the-middle attacks harder. It does not protect against compromised app code, runtime hooks, stolen tokens, backend authorization bugs, or a malicious server with the legitimate pinned key.

## SPKI pinning

Pin the Subject Public Key Info hash rather than the entire certificate. Certificates rotate more often than keys. SPKI pinning is more operationally flexible.

## Backup pins and rotation

Always ship a backup pin. Without one, certificate rotation can break all existing clients. Mobile apps update slowly, so pinning mistakes can cause long-lived outages.

## OkHttp mental model

OkHttp certificate pinning is enforced when a TLS connection is established. If the server chain does not match configured pins, the request fails before application-level handling.

## Operational risks

Pinning is both a security and availability feature. Wrong pins block traffic. Emergency release plans, monitoring, and staged rollout are essential.

## Production checklist

- [ ] SPKI pins are used.
- [ ] Current and backup pins are configured.
- [ ] Rotation procedure is documented.
- [ ] Staging and production hosts are separated.
- [ ] Monitoring catches pinning failures.
- [ ] Emergency plan exists.

## Summary

Certificate pinning is useful, but dangerous when operated casually. ComposeTemplate treats it as optional hardening that must be paired with rotation discipline.
