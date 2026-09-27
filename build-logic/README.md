# Build Logic

`build-logic` is the included Gradle build for generation, architecture, quality, optional infrastructure, and verification conventions.

## Generation

- `create.new.app` copies and rebrands the template.
- `data.strategy.projection` applies strict `remote`, `offline-first`, `local`, or `minimal` topology. Required rewrites must match exactly once; unit tests cover successful, missing, and duplicate matches.
- `scaffold.feature` creates the fixed data/domain/navigation/presentation vertical. Generated screen providers include stable-route restoration; `-PwithDatabase=true` adds Room starters when available.

Network-free projection removes Retrofit/OkHttp/auth and the kotlinx.serialization Retrofit converter. Database-free projection removes Room and database scaffold options. `offline-first` retains both infrastructure families but intentionally does not generate synchronization policy.

## Quality

The test convention registers `jacocoDebugReport` with XML and HTML output. CI runs Android unit tests, build-logic tests, strict docs validation, coverage artifact upload, strategy smoke builds, and benchmark instrumentation.

## Boundaries

App/module boundary plugins protect optional-module removability. Dynamic filesystem discovery remains supported, while literal project dependencies and forbidden imports are checked.

See the [module topology](../wiki/01-module-topology.md), [generator guide](../wiki/05-generator-and-scaffolding.md), [quality guide](../wiki/06-quality-tests-ci.md), and [risk register](../wiki/07-risks-and-gaps.md).
