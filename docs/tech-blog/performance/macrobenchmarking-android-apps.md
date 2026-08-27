# Macrobenchmarking Android Apps the Right Way

## Who this article is for

This article is for Android developers who want to measure startup and critical user journeys with realistic performance tooling.

## What you will learn

- why unit tests are not performance tests
- what Macrobenchmark measures
- why release-like builds matter
- how Macrobenchmark relates to Baseline Profiles
- what mistakes to avoid when interpreting results

## The problem

Performance regressions are easy to introduce and hard to diagnose if you only measure manually. Debug builds, emulator-only testing, and subjective impressions do not produce reliable startup data.

## Why this matters for Android projects

Startup performance affects retention and perceived quality. Compose, DI, navigation, security checks, and initialization all add work to early app execution.

Teams need repeatable measurements for startup and critical flows.

## Macrobenchmark mental model

Macrobenchmark measures the app from outside its process. This lets tests launch the app, capture traces, and report metrics such as startup timing.

It is especially useful for:

- cold startup
- first screen rendering
- navigation flows
- trace analysis
- release-like performance checks

## ComposeTemplate's approach

ComposeTemplate includes a `benchmark` module with Macrobenchmark setup and a benchmark build type initialized from release.

Run:

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

## Relationship with Baseline Profiles

Baseline Profiles tell Android which code paths to optimize. Macrobenchmark measures whether performance is actually acceptable.

Use both:

1. generate/update Baseline Profiles
2. run Macrobenchmark
3. review metrics and traces
4. optimize startup work if needed

## Common mistakes

- measuring debug builds
- relying only on emulators
- running too few iterations
- treating one result as absolute truth
- ignoring trace files
- confusing profile generation with performance validation

## Production checklist

- [ ] benchmarks run against release-like builds
- [ ] startup is measured repeatedly
- [ ] traces are reviewed for regressions
- [ ] Baseline Profiles are updated after startup flow changes
- [ ] critical journeys are benchmarked, not only app launch

## Takeaways

- Macrobenchmark gives repeatable performance signals.
- Debug build measurements are misleading.
- Baseline Profiles and Macrobenchmark solve different parts of the performance workflow.
