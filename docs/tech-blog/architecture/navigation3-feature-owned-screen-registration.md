# Navigation3 with Feature-Owned Screen Registration

## Who this article is for

This article is for Compose developers designing navigation for modular Android apps.

## What you will learn

- why centralized navigation graphs become coupling points
- how route ownership can live inside features
- how `ScreenRegistry` and `IScreenProvider` work
- how bottom-bar items are contributed without a central list

## The problem

Centralized navigation graphs are convenient early. As features grow, the app module starts knowing every route, screen, tab, and parameter.

That weakens feature ownership and makes generated features harder to plug in automatically.

## Why this matters for Android projects

Navigation is often the hidden dependency between features. If it is centralized, every feature change requires app-level edits.

A scalable template should let features own their own route identity and screen registration.

## ComposeTemplate's approach

ComposeTemplate uses:

- `INavigationItem` for route-like objects
- `IBottomBarItem` for tab items
- `INavigationManager` for navigation operations
- `NavigationManager` for `StateFlow`-backed back stack state
- `IScreenProvider` for route-to-screen rendering
- `ScreenRegistry` for resolving screens from providers

## Implementation walkthrough

A feature navigation module defines a route:

```kotlin
@Serializable
data object SettingsRoute : INavigationItem {
    override val route = "route_settings"
}
```

A feature presentation module contributes a screen provider:

```kotlin
class SettingsScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(route: INavigationItem, navigationManager: INavigationManager): Boolean =
        when (route) {
            is SettingsRoute -> {
                SettingsRoute()
                true
            }
            else -> false
        }
}
```

`ScreenRegistry` receives all providers through Hilt multibinding and asks each provider whether it can render the current route.

## Bottom-bar ownership

Bottom-bar items are contributed by feature navigation modules through map multibinding. `NavigationManager` sorts them by key and exposes the final list.

This avoids a central tab registry.

## Design trade-offs

This architecture is more abstract than a simple graph in the app module. Developers must understand route objects, screen providers, Hilt multibinding, and back-stack state.

The benefit is feature ownership and generator-friendly navigation.

## Production checklist

- [ ] routes are feature-owned
- [ ] screen providers are registered via multibinding
- [ ] app module does not manually map every route to every screen
- [ ] bottom-bar items are contributed by features
- [ ] generated features can join navigation without central registry edits

## Takeaways

- Navigation architecture affects modularity.
- Feature-owned route registration reduces app-module coupling.
- Screen providers make generated features easier to wire.
