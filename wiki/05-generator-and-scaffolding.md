# 05 - Generator and Scaffolding Tooling

## `create-new-app`

```bash
./gradlew create-new-app \
  -Pargs='com.example.myapp,MyNewApp' \
  -PdataStrategy=remote \
  -PwithSecrets=true \
  -q --console=plain
```

`dataStrategy` is strict, defaults to `remote`, and accepts exactly:

| CLI value | Label | Network/auth | Room | Start flow |
| --- | --- | ---: | ---: | --- |
| `remote` | Remote only | Yes | No | Onboarding → Login/Home |
| `offline-first` | Offline-first | Yes | Yes | Onboarding → Login/Home |
| `local` | Local only | No | Yes | Onboarding → Home |
| `minimal` | No predefined data infrastructure | No | No | Onboarding → Home |

Every retained feature keeps the fixed `data`, `domain`, `navigation`, and `presentation` modules. “Minimal” removes predefined transport and persistence implementations; it does not remove the data layer.

`withSecrets` is also strict. It applies to network-backed strategies. Local and minimal automatically omit secrets/security because the current secrets subsystem contributes network configuration.

## Projection order

1. Copy consumer files to a sibling directory.
2. Apply the existing secret selection and package/name relocation.
3. Project the selected data strategy.
4. Remove generator-only source and registration.
5. Validate paths and searchable text for forbidden residue.

Network-free outputs remove network/auth modules, transport libraries, auth R8 rules, the network image-loader artifact, secret/security infrastructure, and all corresponding identifiers. Database-free outputs remove the database module, Room catalog entries, Room convention plugin, and database-specific scaffolding support.

Network-backed outputs project auth-aware Splash, Onboarding, and Profile navigation. Network-free outputs keep the source tree’s deletion-safe Onboarding/Home flow.

## `scaffoldFeature`

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

The task always creates the four feature modules. Outputs that selected Room also support:

```bash
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

Database-free outputs contain no Room scaffolding flag or implementation. Feature and application modules are discovered from folders, so scaffolding never patches `settings.gradle.kts` or `app/build.gradle.kts`.

## Consumer projection

Generated projects:

- contain no template wiki, MkDocs config, contribution guide, Pages workflow, local files, `.git`, or generator implementation
- retain consumer CI for lint, tests, and app assembly
- receive a strategy-specific README and build-logic guide
- keep only selected catalog entries, convention plugins, module paths, and source
- fail generation when forbidden strategy residue remains

## Flags

| Flag | Effect |
| --- | --- |
| `dataStrategy` | `remote`, `offline-first`, `local`, or `minimal`; default `remote` |
| `withSecrets` | Strict Boolean for network strategies; default `true` |
| `composetemplate.useNativeSecrets` | Native path inside a generated project that retained secrets |
| `composetemplate.composeCompilerMetricsEnabled` | Compose compiler metrics |
| `composetemplate.composeCompilerReportsEnabled` | Compose compiler reports |

---

[← Previous: 04 - Secrets, Security and Hardening](04-secrets-and-hardening.md) · [Index](README.md) · [Next: 06 - Quality, Tests and CI →](06-quality-tests-ci.md)
