# Macrobenchmarking Android Apps the Right Way

## Who this article is for

This article is for Android developers who want to measure real app performance instead of guessing. It focuses on startup and runtime behavior from the user’s perspective.

## What you will learn

- What Macrobenchmark is
- How it differs from unit tests and microbenchmarks
- Why it should run against release-like builds
- How it relates to Baseline Profiles
- What to measure and how to interpret results

## Why normal tests are not enough

Unit tests answer correctness questions:

```text
Does this function return the expected result?
```

Macrobenchmark answers experience questions:

```text
How long does the app take to start?
How smooth is this journey?
Did our optimization actually help?
```

These are different problems.

A repository can have passing unit tests and still feel slow to users.

## What Macrobenchmark does

Macrobenchmark runs outside your app process. It installs and launches the app, drives user journeys, and records performance metrics.

It can measure:

- cold startup,
- warm startup,
- hot startup,
- frame timing,
- jank,
- critical flows.

Because it observes the app externally, it is closer to what a user experiences.

## Startup modes

Startup mode changes what is being measured.

### Cold startup

The app process does not exist. This is the most expensive startup path and often the most important for first launch.

### Warm startup

The process may exist, but the Activity needs to be recreated.

### Hot startup

The app is already mostly resident and returns to foreground quickly.

You should be explicit about which mode you care about.

## Compilation modes

Macrobenchmark can run with different compilation modes. This matters because Android runtime optimization changes performance.

For example, you may compare:

- no compilation,
- partial compilation with Baseline Profiles,
- full compilation.

This helps answer whether a Baseline Profile is actually improving startup.

## UIAutomator role

Macrobenchmark often uses UIAutomator to interact with the app from outside the process. This is useful because it behaves more like a user or system-level automation than an internal test.

The trade-off is fragility. UI selectors and timing must be stable.

## ComposeTemplate implementation

ComposeTemplate includes a dedicated benchmark module:

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

Relevant areas:

- `benchmark/`
- `baselineprofile/`
- `gradle/libs.versions.toml`

This matters because the template gives generated apps a performance measurement foundation from day one.

## Relationship with Baseline Profiles

Baseline Profiles optimize important code paths. Macrobenchmark measures the result.

A healthy workflow is:

1. Define critical journey.
2. Generate Baseline Profile for that journey.
3. Run Macrobenchmark.
4. Compare startup or frame metrics.
5. Update profile when the journey changes.

## Interpreting results

Do not overreact to a single run. Performance numbers are noisy.

Look for:

- median behavior,
- repeated improvements,
- large regressions,
- trends over time.

Use stable hardware when possible. Avoid comparing results from different devices as if they are equivalent.

## Common mistakes

### Running against debug builds

Debug builds include overhead and do not represent production performance.

### Benchmarking unstable flows

If the UI depends on network or random data, results become noisy.

### Treating benchmarks as absolute truth

Benchmarks are signals. They need context.

### Measuring too much at once

A benchmark should answer a specific question. Start with startup, then add important flows.

## Production checklist

- [ ] Benchmarks run against release-like builds.
- [ ] Startup mode is explicit.
- [ ] Device/emulator environment is stable.
- [ ] Critical flows are deterministic.
- [ ] Results are compared over multiple iterations.
- [ ] Baseline Profile impact is validated with measurements.

## Summary

Macrobenchmark gives Android teams a feedback loop for user-perceived performance. Without it, startup and runtime optimization become guesswork.

ComposeTemplate includes Macrobenchmark support because a production template should make performance measurable from the beginning.
