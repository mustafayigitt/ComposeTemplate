# Navigation3 with Feature-Owned Screen Registration

## Who this article is for

This article is for Compose developers who have felt centralized navigation graphs become difficult to maintain as a project grows.

## What you will learn

- Why navigation is an architecture boundary
- How route-as-data thinking works
- Why feature-owned screen registration scales better
- How Hilt multibinding helps avoid central route lists
- What trade-offs this architecture introduces

## The problem with centralized navigation

Small apps can define navigation in one place. That works until the app grows.

A centralized graph creates problems:

- every new feature edits app-level navigation,
- route strings or route objects become globally managed,
- feature teams must coordinate through a central file,
- bottom-bar setup becomes a shared mutable list,
- removing a feature requires careful cleanup in multiple places.

The app module becomes aware of too many feature details.

## Navigation as data

Navigation becomes easier to reason about when routes are modeled as data instead of stringly typed commands.

A route object can represent:

- destination identity,
- arguments,
- serialization behavior,
- tab membership,
- back-stack state.

Navigation3 supports a more state-driven model where the back stack itself can be treated as data.

## ComposeTemplate navigation model

ComposeTemplate’s navigation layer is built around concepts such as:

- `INavigationItem`
- `INavigationManager`
- `ScreenRegistry`
- `IScreenProvider`
- bottom-bar item registration

The exact implementation can evolve, but the core idea is stable:

> Features own their route and screen registration. The app module orchestrates navigation without knowing every feature implementation.

## ScreenProvider pattern

Instead of a central graph manually mapping every route to every composable, each feature presentation module can contribute a screen provider.

Conceptually:

```kotlin
interface ScreenProvider {
    fun canHandle(route: NavigationItem): Boolean
    @Composable fun Render(route: NavigationItem)
}
```

The app-level registry receives all providers and asks which one can handle the current route.

This keeps screen ownership close to the feature.

## Hilt multibinding

Hilt multibinding is useful when the app needs a set of implementations without manually listing them.

A feature can contribute its provider into a set:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureScreenModule {
    @Binds
    @IntoSet
    abstract fun bindProvider(provider: FeatureScreenProvider): ScreenProvider
}
```

The registry receives `Set<ScreenProvider>` and does not need to know which features exist.

## Bottom-bar registration

The same idea applies to tabs. A feature can contribute bottom-bar metadata through map multibinding rather than editing a central tab list.

This is valuable for templates because generated features can be wired consistently.

## Trade-offs

This architecture is more abstract than simple Navigation Compose examples.

Costs:

- more interfaces,
- more DI setup,
- more indirection during debugging,
- more conventions to document.

Benefits:

- feature ownership,
- easier modularization,
- less app-level coupling,
- scalable screen registration,
- better generator support.

## Common mistakes

### Treating routes as strings

String routes are easy to start with but can become fragile when arguments and refactors grow.

### Letting app know every screen

This defeats feature-owned navigation.

### Mixing navigation events with persistent UI state

Navigation should usually be a one-shot effect, not durable render state.

## Production checklist

- [ ] Routes are type-safe or strongly modeled.
- [ ] Features own route definitions.
- [ ] Screen rendering is registered by feature modules.
- [ ] App module avoids direct screen knowledge.
- [ ] Bottom-bar entries are feature-contributed.
- [ ] Back-stack operations are centralized behind a navigation manager.

## Summary

Navigation is not just UI wiring. In modular apps, it is an ownership boundary.

ComposeTemplate uses feature-owned Navigation3 registration so new features can integrate without turning the app module into a central route registry.
