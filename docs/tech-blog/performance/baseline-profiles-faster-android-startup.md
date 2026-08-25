# Baseline Profiles for Faster Android Startup

## Who this article is for

This article is for Android developers who want to understand startup performance beyond generic advice like "do less work on startup".

## What you will learn

- What ART, JIT, AOT, and profile-guided optimization mean
- What a Baseline Profile actually contains
- How Baseline Profiles help Compose apps
- How Baseline Profiles relate to Macrobenchmark
- How ComposeTemplate wires performance from the template level

## Why Android startup is expensive

Cold startup is not just `MainActivity.onCreate`. Android may need to create a process, load dex files, initialize the Application, build DI graphs, load resources, create the first Activity, initialize Compose, resolve navigation, and render the first frame.

Every framework and abstraction used during this path can add work. Hilt, Compose, navigation, logging, config, and security checks are useful, but they still contribute code paths.

## ART, JIT, and AOT

Android Runtime executes app bytecode. Code can be interpreted, compiled just in time, or compiled ahead of time.

JIT compilation learns hot code while the app runs. That helps later, but users may pay the cost during early execution.

AOT compilation can prepare code earlier, but compiling everything is expensive. Profile-guided optimization solves this by telling the runtime which paths matter most.

## What a Baseline Profile is

A Baseline Profile is a list of important classes and methods. It does not contain business logic. It is a performance hint.

Conceptually:

```text
These startup and critical-flow methods are important.
Optimize them before the user pays the runtime cost.
```

For Compose apps, useful profile paths often include:

- application startup
- first screen navigation
- first composition
- theme and resource setup
- common UI code

## ComposeTemplate approach

ComposeTemplate includes:

- `baselineprofile` module
- `benchmark` module
- Baseline Profile Gradle plugin
- ProfileInstaller dependency
- convention plugin setup

Generation command:

```bash
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

## ProfileInstaller

ProfileInstaller helps ship and install profile rules so ART can use them. It is part of making startup optimization available in production-like environments.

## Common mistakes

- Treating Baseline Profiles as a replacement for startup optimization
- Generating a profile once and never updating it
- Measuring debug builds
- Confusing profile generation with performance validation
- Profiling too many unstable flows

## Production checklist

- [ ] Startup journey is profiled.
- [ ] First critical screen is covered.
- [ ] Profile is regenerated after navigation or dependency changes.
- [ ] Macrobenchmark validates the impact.
- [ ] Measurements use release-like builds.
- [ ] Generated app behavior is considered for template repositories.

## Summary

Baseline Profiles help Android optimize important code paths before users experience runtime compilation cost. ComposeTemplate includes them because startup performance should be a project foundation, not a late-stage optimization task.
