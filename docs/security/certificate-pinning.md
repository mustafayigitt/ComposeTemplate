# Certificate Pinning

Certificate pinning can reduce some man-in-the-middle risk, but it also creates operational risk if not managed carefully.

## Problem

TLS already validates certificates through trusted certificate authorities. Certificate pinning adds an extra constraint: the app only trusts a specific certificate or public key for a host.

That can make some interception attacks harder, but a bad pinning rollout can break production networking for every installed client.

## What to pin

Prefer SPKI public key pins rather than leaf certificate pins. SPKI pins are generally more stable across certificate renewals when the key is retained.

## Primary and backup pins

Always configure at least two pins:

- current primary pin,
- backup pin.

The backup pin gives you a recovery path if the primary certificate/key must rotate.

## Operational risks

Certificate pinning can fail because of:

- certificate rotation,
- CDN or edge-provider changes,
- wildcard host mistakes,
- missing backup pin,
- app versions stuck in the wild,
- emergency backend migration.

Because mobile clients update slowly, a bad pin can remain deployed for a long time.

## ComposeTemplate configuration

Certificate pinning is controlled through secret/configuration values such as:

- `CERTIFICATE_PINNING_ENABLED`,
- `CERTIFICATE_PINS`.

`validateSecrets` checks pin format when pinning is enabled and requires at least primary and backup pins.

## Security boundary warning

Certificate pinning is not backend authorization. It does not prove the user is legitimate, protect server-side data by itself, or replace token validation.

Also avoid treating client-only remote config as a security boundary. If an attacker controls the client, client-side switches can be bypassed.

## Checklist

- [ ] pinning is intentionally enabled or intentionally disabled.
- [ ] at least two pins exist when enabled.
- [ ] pins use `sha256/<base64>` format.
- [ ] rotation plan exists before release.
- [ ] emergency rollback strategy is documented.
- [ ] backend authorization remains the decision point.

## Repository references

- `build-logic/convention/ValidateSecretsPlugin.kt`
- `secrets.properties.example`
- `core/secrets/SecretManager.kt`
