# KSP, Hilt and Build Logic Integration

## Who this article is for

This article is for Android developers using Hilt and KSP across many Gradle modules.

## What you will learn

- why dependency injection setup drifts in multi-module projects
- why KSP compatibility matters
- how convention plugins reduce repeated Hilt setup
- how generated features benefit from centralized DI conventions

## The problem

Hilt setup is repetitive. Each module may need plugins, compiler configuration, dependencies, generated sources, and test dependencies.

If every module does this manually, some modules eventually miss KSP, use incompatible versions, or configure DI differently.

## Why this matters for Android projects

Dependency injection touches nearly every layer: app, core modules, feature data, feature presentation, navigation registration, repositories, use cases, and ViewModels.

Inconsistent DI setup becomes a build problem and an architecture problem.

## KSP and Hilt

Hilt uses annotation processing/code generation. ComposeTemplate uses KSP wiring through build logic so modules get consistent compiler setup.

KSP must stay compatible with the Kotlin version. This compatibility is governed through the version catalog.

## ComposeTemplate's approach

ComposeTemplate centralizes Hilt and KSP setup in:

```text
composetemplate.android.hilt
```

Feature layer plugins can apply or depend on the correct setup for their role.

This keeps feature module build files focused on role and explicit inter-module dependencies.

## Generated features

`scaffoldFeature` creates Hilt-ready presentation code, including ViewModel and screen-provider bindings.

Because generated modules use the same convention plugins, generated DI setup is consistent with existing features.

## Design trade-offs

Centralized DI build logic reduces boilerplate but requires maintainers to understand build-logic code. If a convention plugin hides too much, developers may struggle to debug generated code.

Documentation should clearly map plugins to responsibilities.

## Production checklist

- [ ] Hilt setup is centralized
- [ ] KSP version matches Kotlin compatibility requirements
- [ ] generated features include required DI bindings
- [ ] screen providers are multibound correctly
- [ ] ViewModels use constructor injection
- [ ] CI compiles generated feature modules

## Takeaways

- DI consistency is a build-system concern.
- KSP/Kotlin compatibility must be governed centrally.
- Generated features should receive correct DI setup automatically.
