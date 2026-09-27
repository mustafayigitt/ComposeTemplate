# 06 - Quality, Tests and CI

## Local quality gates

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

Application/library conventions attach import and literal project-dependency boundary checks to normal build/test tasks.

## Five job definitions, eight visible checks

`.github/workflows/ci.yml` defines five job types:

1. Lint
2. Unit Tests
3. Assemble Debug + Release
4. Plug-out
5. Template Smoke matrix

The four-row matrix expands the workflow to eight visible check runs:

- Lint
- Unit Tests
- Assemble Debug + Release
- Plug-out
- Template Smoke (`remote`)
- Template Smoke (`offline-first`)
- Template Smoke (`local`)
- Template Smoke (`minimal`)

## What CI proves

- source-template lint, tests, debug assembly, and release assembly
- a plug-out rebuild after deleting the tested optional path set
- normal four-layer feature scaffolding and compilation
- Room-backed feature scaffolding and compilation
- rejection of an invalid `dataStrategy`
- four strategy-specific consumer generations
- strategy-specific path/text residue cleanup
- `LoginRoute` presence for network strategies and absence for network-free strategies
- debug and release assembly for every strategy
- `remote + withSecrets=false` generation, residue checks, and debug/release assembly

## Exact plug-out set

The plug-out job deletes:

- `core/security`
- `core/analytics`
- `core/database`
- `core/network`
- `core/secrets`
- `feature/auth`
- `benchmark`
- `baselineprofile`

This source-template test complements consumer generation: plug-out protects deletion-safe module boundaries, while the matrix validates the final projected consumer.

## Matrix semantics

| Strategy | Required retained infrastructure |
| --- | --- |
| `remote` | network/auth; no Room |
| `offline-first` | network/auth and Room |
| `local` | Room; no network/auth/secrets |
| `minimal` | no predefined network, auth, Room, secrets, or security |

The matrix proves topology and buildability. For `offline-first`, it does not prove application synchronization semantics.

## Known CI limitations

- Secret bootstrap is duplicated across jobs.
- Scaffold checks repeat in every matrix instance.
- No instrumentation execution.
- No benchmark execution.
- No coverage/report artifact.
- MkDocs strict build/link validation is not currently a pull-request check.
- Workflow YAML remains sensitive to formatting and invisible-character mistakes.

---

[← Previous: 05 - Generator and Scaffolding Tooling](05-generator-and-scaffolding.md) · [Index](README.md) · [Next: 07 - Risks, Gaps and Open Questions →](07-risks-and-gaps.md)
