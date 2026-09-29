# 06 - Quality, Tests and CI

## Local gates

```bash
./gradlew ktlintCheck detekt testDebugUnitTest jacocoDebugReport assembleDebug :app:assembleRelease
./gradlew -p build-logic :convention:test
mkdocs build --strict
```

## Pull-request workflow

CI has five job definitions and eight visible checks: lint/docs, unit tests/coverage, debug+release assembly, plug-out, and four template-smoke strategy rows.

The workflow now:

- uses one `core/secrets/ci/setup.sh` bootstrap in every applicable job;
- runs strict MkDocs validation when `mkdocs.yml` exists;
- runs build-logic projector tests;
- produces and uploads Jacoco XML/HTML artifacts;
- runs feature scaffold checks only in the `remote` matrix row;
- verifies generated screen providers include restoration;
- executes the benchmark instrumentation suite on an API 35 emulator;
- generates, residue-checks, and builds all four data strategies;
- separately proves a secret-free network consumer;
- uses checkout/setup-java/setup-gradle v5 actions.

Plug-out still removes security, analytics, database, network, secrets, auth, benchmark, and baseline-profile paths before rebuilding. The matrix proves topology and buildability; it does not claim generic offline synchronization semantics.

---

[← Previous](05-generator-and-scaffolding.md) · [Index](README.md) · [Next →](07-risks-and-gaps.md)
