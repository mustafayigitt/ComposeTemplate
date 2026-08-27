# Version Catalog and Dependency Governance in Android

## Who this article is for

This article is for Android developers who maintain dependency versions across multi-module projects.

## What you will learn

- why dependency versions drift in Android projects
- how Gradle version catalogs centralize decisions
- why Compose BOM, Kotlin, KSP, and AGP compatibility matter
- how ComposeTemplate treats dependency updates as governance work

## The problem

Android projects often start with dependencies declared directly in module files. As modules grow, versions spread across the repository and upgrades become risky.

Version drift causes problems such as mismatched Compose artifacts, incompatible Kotlin/KSP versions, inconsistent test dependencies, and hard-to-review plugin upgrades.

## Why this matters for Android projects

Android builds combine the Android Gradle Plugin, Kotlin, Compose compiler, KSP, Room, Hilt, Navigation, and many AndroidX libraries. These tools have compatibility relationships.

A version upgrade is not just a text change. It can affect code generation, compiler behavior, generated sources, runtime behavior, and CI stability.

## ComposeTemplate's approach

ComposeTemplate centralizes versions in:

```text
gradle/libs.versions.toml
```

The catalog includes SDK versions, AndroidX, Compose BOM, Navigation3, DataStore, Room, Retrofit, OkHttp, Hilt, Kotlin, KSP, static analysis, testing, benchmark dependencies, Google Play libraries, Coil, and build logic dependencies.

## Library aliases

Library aliases remove coordinates from module files:

```toml
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
```

Modules and convention plugins can use stable aliases instead of raw strings.

## Plugin aliases

Plugin aliases centralize Gradle plugin versions:

```toml
android-application = { id = "com.android.application", version.ref = "android-gradle-plugin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

This makes plugin upgrades easier to review.

## Compose BOM

Compose uses a BOM to align Compose artifacts. This reduces the need to version every Compose dependency independently and lowers mismatch risk across UI modules.

## Kotlin and KSP compatibility

KSP versions are tied to Kotlin versions. When Kotlin changes, KSP should be reviewed immediately.

A safe update flow is:

1. update Kotlin
2. update KSP to the compatible line
3. sync Gradle
4. run tests
5. compile generated features
6. run debug and release builds

## Design trade-offs

A version catalog is another file to maintain, but it makes dependency decisions explicit and reviewable.

For template projects, that reviewability matters because downstream generated apps inherit the dependency baseline.

## Production checklist

- [ ] dependency versions live in the catalog
- [ ] plugin versions live in the catalog
- [ ] Kotlin and KSP are upgraded together
- [ ] Compose artifacts are aligned through the BOM
- [ ] CI validates dependency upgrades
- [ ] Renovate or similar tooling is configured intentionally

## Takeaways

- Version catalogs are dependency governance, not just syntax cleanup.
- Android dependency upgrades must respect compatibility relationships.
- Template dependency choices become defaults for generated apps.
