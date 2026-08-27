# Building a Production-Grade Jetpack Compose Template Generator

## Who this article is for

This article is for Android developers and platform engineers who want to turn a good project setup into a reusable project generator.

## What you will learn

- why a starter app is different from a template generator
- why Android project identity is hard to rename manually
- what a generator must rewrite and exclude
- why generated output must be tested in CI
- how ComposeTemplate positions generation as a core feature

## The problem

Most teams eventually create a starter project. It contains preferred architecture, dependencies, UI theme, network setup, and testing conventions.

That helps, but a starter project still leaves a dangerous workflow: clone the repository, rename things manually, delete template-only code, update package paths, and hope nothing was missed.

Android project identity is spread across many places:

- Kotlin package declarations
- source directory paths
- Gradle namespace
- application id
- XML resources
- manifests
- ProGuard files
- documentation
- build logic
- native configuration

A production template cannot rely on manual rename instructions.

## Why this matters for Android projects

The first day of a project sets the long-term baseline. If generation is inconsistent, every app created from the template starts with hidden debt.

Common failure modes include:

- source folders do not match package names
- stale template package references remain
- local secret files are copied
- generator-only tasks ship with the generated app
- JNI/native bindings break after package rename
- generated apps are not validated in CI

A template generator must treat project creation as a build-supported workflow.

## Common approaches

### Copy and rename manually

This is easy to start but fragile. It relies on a checklist and usually misses edge cases.

### External script

A script can improve repeatability, but it may drift from Gradle and repository conventions.

### Gradle task inside build logic

A Gradle-backed generator can live with the project, use the same repository assumptions, and be validated by the same CI pipeline.

ComposeTemplate uses this approach.

## ComposeTemplate's approach

ComposeTemplate exposes:

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

The task is implemented in `CreateNewAppPlugin.kt` and performs a deterministic generation flow:

1. validate app id and app name
2. create a sibling target directory
3. copy template files
4. exclude local-only and generated files
5. rewrite package and app-name references
6. move source directories to the new package
7. remove generator-only code from the generated project
8. print next steps

## Exclusion is a security feature

The generator excludes files such as:

- `.git`
- `.gradle`
- `.idea`
- `.kotlin`
- `.cxx`
- `.externalNativeBuild`
- `local.properties`
- `secrets.properties`
- build outputs
- APK/AAB artifacts
- keystores
- logs

This prevents local developer state and secret material from becoming part of a generated app.

## Native rebrand safety

Native/JNI code can be fragile when package names change. Traditional JNI method names encode the Java package path. If a generator renames packages, convention-based JNI bindings may break.

ComposeTemplate's native secret approach is designed to survive rebranding by avoiding package-name-dependent native method exports.

## CI validation

A generator should be tested like product code. ComposeTemplate's CI runs `create-new-app` and verifies generated output does not include local secrets or Git metadata.

This matters because template generation can break even when the original app still compiles.

## Design trade-offs

A Gradle generator is more complex than a shell script. It requires maintaining build logic and keeping generation behavior aligned with the repository.

The trade-off is worth it because generation becomes versioned, reviewable, and testable with the rest of the build.

## Production checklist

- [ ] generation is deterministic
- [ ] package declarations are rewritten
- [ ] source directories are moved
- [ ] app name references are updated
- [ ] local secrets are excluded
- [ ] generated app does not keep generator-only code
- [ ] native bindings survive package rename
- [ ] CI validates generated output

## Takeaways

- A starter app shows a pattern; a generator creates a new project identity.
- Android rename workflows are too broad for manual instructions.
- Excluding local files is part of the security model.
- Template repositories must test generated apps, not only themselves.
