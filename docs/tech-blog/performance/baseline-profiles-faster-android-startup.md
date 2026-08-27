# Baseline Profiles for Faster Android Startup

## Who this article is for

This article is for Android developers who want startup performance infrastructure from the beginning of a project.

## What you will learn

- what ART, JIT, AOT, and profile-guided optimization mean
- what a Baseline Profile contains
- why Compose apps benefit from startup profiles
- how Baseline Profiles relate to Macrobenchmark
- why template-generated apps should keep performance tooling

## The problem

Cold startup is not only `MainActivity.onCreate`. Android may create a process, load dex files, initialize the application, build dependency graphs, restore configuration, resolve navigation, create the first composition, and draw the first frame.

Modern app foundations such as Compose, Hilt, navigation, security checks, logging, and configuration are valuable, but they also add code paths to startup.

## Why this matters for Android projects

Startup performance affects user perception immediately. If a template includes architecture and infrastructure but no performance measurement, generated apps can inherit hidden startup cost.

## ART, JIT, and AOT

Android Runtime can interpret code, compile just in time, or use ahead-of-time compiled code. Profile-guided optimization tells the runtime which methods/classes are important enough to optimize early.

A Baseline Profile is the app's way of saying:

```text
These startup and critical-flow paths matter. Optimize them before users pay the runtime cost.
```

## ComposeTemplate's approach

ComposeTemplate includes:

- `baselineprofile` module
- `benchmark` module
- Baseline Profile Gradle plugin
- ProfileInstaller dependency
- benchmark build type

Generate profiles with:

```bash
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

## What to profile

Good profile candidates include:

- cold startup
- first screen render
- navigation to primary destinations
- theme setup
- core UI components
- critical business flows

## Relationship with Macrobenchmark

Baseline Profiles are optimization inputs. Macrobenchmark is measurement.

A strong workflow is:

1. generate/update Baseline Profile rules
2. run Macrobenchmark
3. inspect metrics and traces
4. optimize startup work
5. repeat after significant navigation or startup changes

## Design trade-offs

Performance tooling adds modules and device requirements. Macrobenchmarks should run on physical devices and release-like builds, which is more work than a normal unit test.

The payoff is early visibility into startup quality.

## Production checklist

- [ ] startup journey is profiled
- [ ] first critical screen is covered
- [ ] profile is regenerated after startup/navigation changes
- [ ] Macrobenchmark validates the effect
- [ ] measurements use release-like builds
- [ ] generated apps keep performance infrastructure available

## Takeaways

- Baseline Profiles reduce runtime compilation cost on important paths.
- They are not a substitute for removing unnecessary startup work.
- Templates should include performance tooling before apps need emergency optimization.
