# Navigation3 with Feature-Owned Screen Registration

## Who this article is for

This article is for Compose developers who want navigation to scale across feature modules without turning the app module into a central route registry.

## What you will learn

- Why navigation is an architecture boundary
- How route-as-data thinking works
- Why centralized graphs become painful
- How feature-owned screen registration works
- How Hilt multibinding helps with modular discovery

## The problem with centralized navigation graphs

Central navigation is easy at first. A single file maps every route to every screen. But in a modular project, it creates coupling:

- adding a screen requires app-level edits
- removing a feature requires central cleanup
- bottom-bar items become a shared list
- feature teams coordinate through one graph
- route definitions drift away from the feature that owns them

The app module gradually learns too much.

## Navigation as state and data

Navigation becomes easier to reason about when a destination is modeled as data, not just a string. A route can carry identity, arguments, serialization behavior, and back-stack state.

Navigation3 encourages a state-oriented model where the back stack is a list of navigation items. This fits Compose because UI can be derived from state.

## ComposeTemplate model

ComposeTemplate uses concepts such as:

- `INavigationItem` for route-like items
- `INavigationManager` for back-stack operations
- `ScreenRegistry` for resolving routes to screens
- `IScreenProvider` for feature-owned rendering
- bottom-bar item registration for tab ownership

The key idea is:

> Features contribute screens. The app resolves them.

## ScreenProvider pattern

Instead of a central `when(route)` block, each feature can contribute a provider:

```kotlin
interface ScreenProvider {
    fun canHandle(item: INavigationItem): Boolean

    @Composable
    fun Render(item: INavigationItem)
}
```

The registry receives all providers and asks which one can render the current route. This keeps route knowledge close to the feature.

## Hilt multibinding

Hilt multibinding allows modules to contribute implementations into a collection:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthScreenModule {
    @Binds
    @IntoSet
    abstract fun bindAuthScreenProvider(
        provider: AuthScreenProvider,
    ): ScreenProvider
}
```

The app can inject `Set<ScreenProvider>` without manually listing every feature.

## Bottom-bar registration

Tabs can use the same idea. A feature contributes tab metadata through DI instead of editing a central list. This is especially useful for generated features because the scaffolder can follow the same contract.

## Trade-offs

This design has more indirection than a simple graph. Debugging may require following DI bindings and registry resolution. But it keeps feature ownership strong and makes modular growth easier.

## Common mistakes

- Treating routes as untyped strings
- Letting the app module know every screen implementation
- Mixing navigation effects into persistent UI state
- Creating providers that handle too many unrelated routes

## Production checklist

- [ ] Routes are strongly modeled.
- [ ] Feature modules own route definitions.
- [ ] Screen providers are registered by feature modules.
- [ ] App module orchestrates but does not know every screen.
- [ ] Bottom-bar items are contributed by features.
- [ ] Back-stack mutations are centralized behind a navigation manager.

## Summary

Navigation is not just UI plumbing. In a modular Compose app, it is a feature boundary. ComposeTemplate uses feature-owned Navigation3 registration to keep navigation scalable and generator-friendly.
