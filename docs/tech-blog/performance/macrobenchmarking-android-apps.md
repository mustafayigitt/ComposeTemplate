# Macrobenchmarking Android Apps the Right Way

Macrobenchmark measures app performance from the outside, closer to how users experience the app.

## Problem

Unit tests do not measure startup time, frame timing, or runtime performance. They validate correctness, not user-perceived speed.

## ComposeTemplate approach

ComposeTemplate includes a dedicated `benchmark` module and Macrobenchmark dependencies.

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

## Relationship with Baseline Profiles

Macrobenchmark can validate performance and help generate/verify baseline-profile benefits. Baseline Profiles optimize code paths; Macrobenchmark measures whether the optimization improves real behavior.

## Takeaway

Macrobenchmark gives performance work a feedback loop. Without measurement, performance optimization is guesswork.
