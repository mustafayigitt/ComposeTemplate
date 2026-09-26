# 06 - Quality, Tests and CI

## Local quality gates

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

Application and library convention plugins attach import and literal project-dependency boundary checks to normal build/test tasks. The checks prevent retained modules from accumulating direct source or build-file coupling to removable modules.

## CI jobs

The pull-request workflow has five jobs:

1. Lint — ktlint and detekt
2. Unit Tests
3. Assemble Debug + Release
4. Plug-out — deletes the proven optional module set and assembles the app
5. Template Smoke — exercises scaffolding and generated projects

## Data-strategy matrix

Template Smoke is the executable product contract. It generates four sibling applications:

- `remote`
- `offline-first`
- `local`
- `minimal`

Each generated project must assemble debug and release. The job additionally checks strategy-specific path and text residue:

- remote has network/auth and no database
- offline-first has network/auth and database
- local has database and no network/auth/secrets
- minimal has neither network/auth nor database/secrets

Network-backed output must contain the Login destination flow. Network-free output must contain the direct Home flow and no auth route reference.

The matrix complements raw plug-out testing: plug-out protects folder-level removability in the source template, while generation proves the final consumer projection, catalog cleanup, convention cleanup, documentation, and release build.

## Scaffolding smoke

The template repository still verifies a normal four-module feature and a Room-backed feature before project generation. Database-free generated outputs intentionally no longer expose `-PwithDatabase=true`.

## Known CI limitations

- Secret bootstrap is duplicated across jobs.
- No instrumentation or benchmark execution.
- No coverage/report artifact upload.
- Workflow changes require careful YAML review because invisible characters can suppress all checks.

---

[← Previous: 05 - Generator and Scaffolding Tooling](05-generator-and-scaffolding.md) · [Index](README.md) · [Next: 07 - Risks, Gaps and Open Questions →](07-risks-and-gaps.md)
