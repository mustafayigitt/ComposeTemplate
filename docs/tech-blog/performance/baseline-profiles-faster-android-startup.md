# Baseline Profiles for Faster Android Startup

## Who this article is for

This article is for Android developers who want to understand why app startup can feel slow and how Baseline Profiles help improve startup performance in production builds.

You do not need to be a performance expert. You should be comfortable with Android builds, Activities, and the idea that Android apps run bytecode through the Android Runtime.

## What you will learn

- What happens during Android startup
- How ART, JIT, AOT and profile-guided optimization relate to each other
- What a Baseline Profile is and what it is not
- How Baseline Profiles work with ProfileInstaller
- How ComposeTemplate wires Baseline Profiles
- Common mistakes and production checklist

## Why startup is slower than it looks

When a user taps an app icon, Android does much more than call `MainActivity.onCreate`.

A cold start may involve:

- creating a new app process,
- loading dex files,
- loading classes,
- initializing the `Application`,
- building dependency injection graphs,
- initializing logging, config and security layers,
- creating the first Activity,
- running Compose runtime setup,
- rendering the first frame.

Modern Android apps often use many abstractions: Hilt, Compose, navigation, networking, persistence, and analytics. These are good tools, but they add code paths that may execute during startup.

If the runtime has to interpret or just-in-time compile too much code during these early paths, startup gets slower.

## ART, JIT and AOT in simple terms

Android apps are compiled into dex bytecode. At runtime, Android Runtime — ART — executes that code.

There are several ways code can become optimized machine code:

### Interpretation

The runtime can interpret bytecode directly. This is flexible, but slower.

### JIT compilation

JIT means just-in-time compilation. ART observes code while the app runs and compiles hot methods at runtime.

This improves future execution, but it means the user may pay some cost before the runtime knows what is hot.

### AOT compilation

AOT means ahead-of-time compilation. Code is compiled before it runs, usually at install time or later during background optimization.

AOT can improve startup, but compiling everything is expensive in time and storage.

### Profile-guided optimization

Profile-guided optimization gives the runtime a hint:

> These classes and methods are important. Optimize them early.

That is where Baseline Profiles come in.

## What a Baseline Profile actually is

A Baseline Profile is not Kotlin code. It is not a benchmark result. It is not a magic flag that makes every app fast.

It is a list of important classes and methods that represent critical user journeys, such as startup or navigating to the first screen.

Conceptually, it says:

```text
These code paths matter for the first user experience.
Please prioritize optimizing them.
```

For a Compose app, these paths can include:

- app startup code,
- dependency injection setup,
- first navigation route,
- first screen composition,
- theme and resource loading,
- frequently used UI code.

## Baseline Profiles and Compose

Compose apps can benefit from Baseline Profiles because first composition touches many runtime and UI-related paths. Even if business logic is small, the UI framework still has work to do.

Baseline Profiles can reduce the amount of runtime compilation needed during startup and early rendering.

This does not replace good startup hygiene. You should still avoid heavy synchronous work in startup. Baseline Profiles optimize important paths; they do not excuse unnecessary work.

## ProfileInstaller

`androidx.profileinstaller` helps install baseline profile rules so the runtime can use them on devices.

In practice, a production app includes a baseline profile artifact, and ProfileInstaller helps make it available to ART.

ComposeTemplate includes ProfileInstaller as part of making startup performance a default project concern.

## ComposeTemplate implementation

ComposeTemplate includes two dedicated performance modules:

- `baselineprofile`
- `benchmark`

The Baseline Profile generation command is:

```bash
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

The template also has a convention plugin for baseline profile generator setup, keeping module configuration consistent with the rest of the build system.

Relevant areas:

- `baselineprofile/`
- `benchmark/`
- `build-logic/convention/BaselineProfileGeneratorConventionPlugin.kt`
- `gradle/libs.versions.toml`

## What should go into a profile?

A good baseline profile should cover stable, important flows:

- app launch,
- first screen rendering,
- login entry point,
- main tab navigation,
- critical above-the-fold UI.

Do not try to profile every screen. The goal is to optimize high-value paths, not generate a giant list.

## Common mistakes

### Treating Baseline Profiles as a replacement for optimization

If startup does too much work, a profile will not fix the architecture. Move unnecessary work off startup first.

### Generating once and forgetting it

Profiles become stale when startup paths change. Update them when navigation, DI, startup screens, or major libraries change.

### Measuring debug builds

Debug builds are not representative. Use release-like builds when evaluating performance.

### Confusing generation with measurement

Generating a profile and proving that startup improved are different tasks. Use Macrobenchmark to measure.

## Production checklist

- [ ] Startup path is covered by a baseline profile.
- [ ] Profile generation runs on a real device or stable emulator.
- [ ] Startup work is kept minimal.
- [ ] Profile is regenerated after major navigation or dependency changes.
- [ ] Macrobenchmark is used to validate impact.
- [ ] Release-like builds are used for measurements.

## Summary

Baseline Profiles help Android optimize important code paths before users pay runtime compilation costs. They are especially useful for modern Compose apps, where startup and first composition touch many framework and app-level paths.

ComposeTemplate includes Baseline Profile support because performance should be part of the project foundation, not a late-stage cleanup task.
