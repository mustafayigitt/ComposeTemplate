# Macrobenchmarking Android Apps the Right Way

## Who this article is for

This article is for Android developers who want to measure real user-perceived performance instead of guessing.

## What you will learn

- What Macrobenchmark measures
- Why it differs from unit tests
- How startup modes and compilation modes affect results
- How it validates Baseline Profile impact
- How to avoid misleading benchmark data

## Why normal tests are not enough

Unit tests validate correctness. They do not answer whether startup is fast, first frame is delayed, or a user journey is smooth.

Macrobenchmark runs outside the app process, installs or launches the app, drives journeys, and records performance metrics. That makes it closer to real user experience.

## What Macrobenchmark can measure

Macrobenchmark can measure:

- cold startup
- warm startup
- hot startup
- frame timing
- jank
- critical UI journeys

## Startup modes

Cold startup means the app process does not exist. Warm startup means the process may exist but the Activity needs recreation. Hot startup means the app is mostly resident and returns to foreground.

Each answers a different question, so reports should name the startup mode explicitly.

## Compilation modes

Compilation mode determines how much runtime optimization exists before measurement. This matters when comparing Baseline Profile impact.

A useful workflow is:

1. measure without profile support,
2. generate or update Baseline Profile,
3. measure with profile-guided compilation,
4. compare results over multiple iterations.

## ComposeTemplate approach

ComposeTemplate includes a dedicated `benchmark` module:

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

This is paired with `baselineprofile` so the template supports both optimization and measurement.

## Common mistakes

- Running benchmarks on debug builds
- Measuring network-dependent flows
- Comparing different devices as if they are equivalent
- Drawing conclusions from one run
- Measuring too many things in one benchmark

## Production checklist

- [ ] Benchmarks run against release-like variants.
- [ ] Startup mode is explicit.
- [ ] Test device is stable.
- [ ] Flows are deterministic.
- [ ] Results are compared over repeated iterations.
- [ ] Baseline Profile impact is measured, not assumed.

## Summary

Macrobenchmark provides the feedback loop for Android performance work. ComposeTemplate includes it so performance can be measured from the beginning, not after users complain.
