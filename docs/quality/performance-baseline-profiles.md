# Performance and Baseline Profiles

ComposeTemplate includes startup performance infrastructure from the beginning through Baseline Profile and Macrobenchmark modules.

## Problem

Android startup performance is often addressed late, after architecture, dependency injection, navigation, and UI framework decisions are already in place.

By then, it is harder to understand what changed and why startup regressed.

ComposeTemplate includes performance tooling in the template so generated apps can measure and optimize early.

## Modules

| Module | Purpose |
|---|---|
| `baselineprofile` | generates Baseline Profile rules |
| `benchmark` | runs Macrobenchmark measurements |

## Baseline Profiles

A Baseline Profile tells Android Runtime which code paths are important enough to pre-optimize.

For Compose apps, useful startup paths often include:

- application initialization,
- dependency injection setup,
- first navigation destination,
- theme setup,
- first composition,
- critical user journeys.

Generate profiles with:

```bash
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

## Macrobenchmark

Macrobenchmark measures app behavior from outside the process. It is useful for startup timing, trace capture, and release-like performance validation.

Run benchmarks with:

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

## Release-like measurement

Performance should not be judged from debug builds. ComposeTemplate includes a benchmark build type initialized from release so measurements better reflect optimized builds.

## Interpreting results

Macrobenchmark output can include startup metrics such as:

- TTID: Time To Initial Display,
- TTFD: Time To Full Display, when reported,
- trace files for deeper investigation.

Traces can be opened in Android Studio Profiler or Perfetto.

## Checklist

- [ ] startup journey is covered by a Baseline Profile.
- [ ] critical user journeys are measured with Macrobenchmark.
- [ ] measurements use release-like builds.
- [ ] profiles are regenerated after startup/navigation changes.
- [ ] benchmark results are reviewed before performance-sensitive releases.
- [ ] generated apps keep performance infrastructure available.

## Repository references

- `baselineprofile`
- `benchmark`
- `benchmark/README.md`
- `app/build.gradle.kts`
