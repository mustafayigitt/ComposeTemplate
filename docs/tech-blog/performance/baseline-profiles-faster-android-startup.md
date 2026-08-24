# Baseline Profiles for Faster Android Startup

Baseline Profiles help Android apps start faster by telling the runtime which code paths should be optimized ahead of time.

## Problem

Android apps may pay runtime compilation costs during startup and critical flows. This can affect cold-start time, first-screen rendering, and perceived performance.

## ComposeTemplate approach

ComposeTemplate includes:

- `baselineprofile` module
- Baseline Profile Gradle plugin
- ProfileInstaller dependency
- commands for profile generation

```bash
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

## Takeaway

Baseline Profiles move startup optimization into the project foundation instead of leaving it as a late-stage performance task.
