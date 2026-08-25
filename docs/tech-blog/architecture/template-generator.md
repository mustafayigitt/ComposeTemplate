# Building a Production-Grade Jetpack Compose Template Generator

## Who this article is for

This article is for Android developers and platform engineers who want to build reusable project foundations instead of repeatedly copying starter apps.

## What you will learn

- Why a template generator is different from a sample app
- What must be automated for a generated Android app to be trustworthy
- How ComposeTemplate thinks about package rewrite, cleanup, architecture, and CI
- Common mistakes when building Android templates

## Sample app vs template generator

A sample app demonstrates an implementation. A template generator produces a new project that should be independently buildable, maintainable, and free of template-only baggage.

That difference matters. A sample app can leave manual rename instructions in a README. A generator must automate the rename. A sample app can keep demonstration code. A generator must produce a clean baseline developers can own.

## The real problem

Creating a new Android app is not just creating `MainActivity`.

A production-ready starter usually needs:

- package and namespace setup,
- application name replacement,
- Gradle module wiring,
- convention plugins,
- CI workflows,
- local secret exclusions,
- signing placeholders,
- architecture examples,
- test setup,
- documentation,
- feature scaffolding.

Manual clone-and-rename flows are fragile because package names and app names appear in Kotlin, XML, Gradle, resources, manifests, and sometimes native code.

## ComposeTemplate approach

ComposeTemplate is designed as a generator-backed template. The main generation flow is:

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

The generator copies the template, rewrites project identifiers, moves source directories, excludes local-only files, and removes generator-specific build logic from the generated output.

## Why cleanup matters

Generated apps should not keep template-only internals unless they are useful to the app. If a generated project still contains the generator that created it, developers inherit maintenance burden they did not ask for.

## Template quality checklist

- [ ] Generated app builds without manual package fixes.
- [ ] Local files such as `local.properties` and `secrets.properties` are not copied.
- [ ] Template-only tasks are removed from generated output.
- [ ] Native bindings survive package rename.
- [ ] CI validates generated output, not just the template source.
- [ ] Documentation explains what was generated and why.

## Common mistakes

### Treating README steps as automation

If a step is required every time, automate it.

### Forgetting native and resource references

Package names can appear outside Kotlin files.

### Not testing the generated app

The generated app is the product. CI should prove it works.

## Summary

A production-grade template is a system: architecture, build logic, generator tasks, quality gates, security guardrails, performance tooling, and documentation. ComposeTemplate treats generation as a first-class engineering problem, not a copy-paste convenience.