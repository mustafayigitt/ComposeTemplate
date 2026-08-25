# KSP, Hilt and Build Logic Integration

## Who this article is for

This article is for Android developers using Hilt, annotation processing, and multiple Gradle modules.

## What you will learn

- What KSP does
- Why DI setup should be centralized
- How Hilt multibinding supports modular architecture
- Why build logic matters for generated code tools

## What KSP is

KSP stands for Kotlin Symbol Processing. It lets libraries inspect Kotlin symbols and generate code during compilation.

Many Android libraries use code generation. Hilt uses generated components and factories. Room uses generated database and DAO implementations.

Because generated code participates in compilation, KSP setup must be correct in every module that needs it.

## The problem in multi-module projects

Without convention plugins, each module repeats:

- Hilt plugin application,
- KSP plugin application,
- compiler dependency setup,
- generated source configuration,
- test dependency setup.

One missing line can break the module or produce confusing errors.

## ComposeTemplate approach

ComposeTemplate centralizes Hilt and KSP setup through convention plugins. Feature modules apply layer-specific plugins, and the build logic decides which dependencies and processors are needed.

This keeps module build files short and consistent.

## Hilt multibinding

Multibinding allows multiple modules to contribute implementations into a set or map.

That is useful for modular features:

```kotlin
@Binds
@IntoSet
abstract fun bindProvider(provider: FeatureScreenProvider): ScreenProvider
```

The app can receive `Set<ScreenProvider>` without manually knowing each feature provider.

## Why this matters for templates

Generated features should be wired the same way as hand-written features. Centralized build logic ensures scaffolded modules get the same Hilt/KSP setup as existing modules.

## Common mistakes

- Applying Hilt plugin but forgetting compiler dependency.
- Updating Kotlin without matching KSP.
- Creating feature modules that bypass convention plugins.
- Using DI as a service locator instead of dependency wiring.

## Production checklist

- [ ] Hilt setup is centralized.
- [ ] KSP version matches Kotlin.
- [ ] Generated feature modules apply the same conventions.
- [ ] Multibinding is used for feature registration where appropriate.
- [ ] CI compiles scaffolded features.

## Summary

KSP and Hilt are not just library choices. In a modular project, they are part of the build architecture. ComposeTemplate centralizes their setup to keep feature modules reliable and generator-friendly.