# Performance and Baseline Profiles

ComposeTemplate includes performance infrastructure from the beginning.

## Modules

- `baselineprofile`
- `benchmark`

## Commands

```bash
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

## Goals

- Improve startup time
- Improve first-screen experience
- Reduce runtime JIT cost
- Measure critical user journeys
