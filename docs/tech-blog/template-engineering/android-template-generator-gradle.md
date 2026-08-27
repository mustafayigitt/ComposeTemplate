# Building an Android Template Generator with Gradle

## Who this article is for

This article is for Android developers building reusable app foundations, internal starter kits, or project generators.

## What you will learn

- why clone-and-rename is fragile
- what Android project generators must rewrite
- why generation belongs in reviewed build logic
- how ComposeTemplate validates generated apps in CI

## The problem

Android project identity is not stored in one file. It appears in Kotlin packages, source folders, Gradle namespace, application id, manifests, XML resources, docs, and sometimes native code.

A manual rename guide will eventually miss something.

## Why this matters for Android projects

A template generator creates the first commit of future apps. If it copies local state or leaves stale package references, every generated app starts with hidden debt.

## Common approaches

### Manual instructions

Easy to document but error-prone.

### Script outside Gradle

More repeatable, but can drift from the build and project model.

### Gradle task in build logic

Lives with the project, is versioned with the template, and can be tested in CI.

## ComposeTemplate's approach

ComposeTemplate implements generation through `CreateNewAppPlugin`:

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

The task validates inputs, creates a sibling directory, copies files, rewrites package/app names, moves source directories, and removes generator-only code from the generated project.

## Exclusion strategy

The generator excludes local-only files and generated artifacts such as `.git`, `.gradle`, `.idea`, `local.properties`, `secrets.properties`, build outputs, APK/AAB files, keystores, and logs.

This is a security and hygiene feature, not just cleanup.

## Rebrand safety

Generated apps need their own package identity. Source directories and package declarations must match. Native/JNI code must also survive rebranding, which is why package-name-sensitive native binding patterns should be avoided.

## CI validation

ComposeTemplate CI runs `create-new-app` and verifies that local secrets and Git metadata are not copied into the generated app.

## Design trade-offs

A Gradle-based generator is more complex than a simple shell script, but it keeps generation close to the build and makes it reviewable.

## Production checklist

- [ ] generator validates app id and app name
- [ ] package references are rewritten
- [ ] source directories are moved
- [ ] local-only files are excluded
- [ ] generator-only code is removed
- [ ] generated app is tested in CI
- [ ] native/package rename edge cases are considered

## Takeaways

- A template generator is product code.
- Project creation should be deterministic and tested.
- Local file exclusion prevents serious downstream mistakes.
