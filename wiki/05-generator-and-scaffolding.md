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

| Value | Network/auth | Room | Start flow |
| --- | ---: | ---: | --- |
| `remote` | Yes | No | Onboarding → Login/Home |
| `offline-first` | Yes | Yes | Onboarding → Login/Home |
| `local` | No | Yes | Onboarding → Home |
| `minimal` | No | No | Onboarding → Home |

Every retained feature keeps `data`, `domain`, `navigation`, and `presentation`. `minimal` removes predefined transport and persistence implementations; it does not remove feature data layers.

`withSecrets` is a strict Boolean for network-backed strategies. Local/minimal remove secrets/security automatically.

## Projection pipeline

1. Copy consumer files to a sibling project.
2. Exclude `.git`, local properties/secrets, build output, template wiki/MkDocs/contribution material, and template-only workflows.
3. Rebrand package and application names.
4. Apply data strategy and effective secret selection.
5. Project strategy-specific navigation and scaffolding.
6. Remove generator source/registration.
7. Validate forbidden paths and searchable text residue.
8. Write strategy-specific consumer README and build-logic guidance.

Network-free outputs remove transport/auth modules, libraries, rules, conventions, navigation references, and secret/security infrastructure. Database-free outputs remove Room modules, catalog entries, conventions, starter generation, and identifiers.

## `scaffoldFeature`

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

The task always creates four modules. Database-backed consumers also retain:

```bash
./gradlew scaffoldFeature \
  -PfeatureName=settings \
  -PwithDatabase=true
```

That option emits a starter Room entity and DAO. Database-free consumers do not contain the flag or Room-specific scaffold code.

Because settings and app dependencies are discovered from module folders, scaffolding does not patch `settings.gradle.kts` or maintain a static app dependency list.

## Generated-consumer guarantees

Generated projects:

- contain only selected infrastructure and no template generator/wiki residue
- retain consumer CI for lint, unit tests, and app assembly
- receive strategy-specific documentation
- fail generation if forbidden strategy residue remains
- preserve the fixed feature-layer architecture
- assemble debug and release in the template’s strategy matrix

These guarantees cover structure and buildability, not product completeness. Generated apps still require real domain behavior, backend contracts, configuration, branding, signing, migrations, and runtime/UI tests.

## Flags

| Flag | Effect |
| --- | --- |
| `dataStrategy` | Strict `remote`, `offline-first`, `local`, or `minimal`; default `remote` |
| `withSecrets` | Strict Boolean for network strategies; default `true` |
| `composetemplate.useNativeSecrets` | Native secret path in a consumer that retained secrets |
| `composetemplate.composeCompilerMetricsEnabled` | Compose compiler metrics |
| `composetemplate.composeCompilerReportsEnabled` | Compose compiler reports |

---

[← Previous: 04 - Secrets, Security and Hardening](04-secrets-and-hardening.md) · [Index](README.md) · [Next: 06 - Quality, Tests and CI →](06-quality-tests-ci.md)
