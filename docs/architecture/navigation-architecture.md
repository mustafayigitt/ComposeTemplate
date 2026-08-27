# Navigation Architecture

Navigation in ComposeTemplate is feature-owned, type-safe, and built around Navigation3 concepts.

The design avoids a large centralized navigation graph by letting features contribute routes and screen providers.

## Problem

Central navigation graphs often become coupling points. In a growing app, the app module can end up knowing every route, screen, tab, and parameter detail.

That causes several problems:

- adding a feature requires app-level route edits,
- bottom-bar ownership becomes centralized,
- feature modules are less independent,
- route-to-screen mapping becomes harder to test,
- generated features require manual app navigation updates.

ComposeTemplate distributes ownership back to features.

## Building blocks

| Component | Role |
|---|---|
| `INavigationItem` | route-like navigation item |
| `IBottomBarItem` | navigation item with bottom-bar metadata |
| `INavigationManager` | back-stack and navigation operations contract |
| `NavigationManager` | `StateFlow`-backed implementation |
| `IScreenProvider` | feature-owned route-to-screen renderer |
| `ScreenRegistry` | resolves routes by asking registered providers |

## Route ownership

Feature navigation modules define route objects. Routes implement `INavigationItem` and are serializable when needed.

A generated route looks like:

```kotlin
@Serializable
data object SettingsRoute : INavigationItem {
    override val route: String = "route_settings"
}
```

This keeps route identity close to the feature.

## Back stack management

`NavigationManager` owns a `StateFlow<List<INavigationItem>>` back stack.

It supports operations such as:

- `navigate(route)`,
- `navigateBack()`,
- `navigateOver(route, over)`,
- `navigateToTop(route)`,
- `navigateBackToRoot()`,
- `selectTab(selected)`.

This makes navigation state observable and testable.

## Screen registration

A feature presentation module contributes an `IScreenProvider` through Hilt multibinding.

The provider checks whether it can render a route:

```kotlin
class SettingsScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager,
    ): Boolean = when (route) {
        is SettingsRoute -> {
            SettingsRoute()
            true
        }
        else -> false
    }
}
```

`ScreenRegistry` receives all providers as a set and asks them to resolve the current route. If none can render it, a fallback "Screen not found" UI is shown.

## Bottom-bar registration

Bottom-bar items are contributed from feature navigation modules through Hilt map multibinding. `NavigationManager` sorts the map by key and exposes the result as `bottomBarItems`.

This avoids maintaining a single tab list in the app module.

## Why this helps generated features

Because `scaffoldFeature` generates route and screen-provider boilerplate, a generated feature can join the navigation system without manual route registry edits.

The app module wires modules together; features own their navigation entry points.

## Trade-offs

This design is more abstract than a single navigation graph. Developers need to understand the relationship between route objects, screen providers, Hilt multibinding, and the screen registry.

The benefit is stronger feature ownership and a navigation model that scales better with generated modules.

## Checklist

- [ ] routes live in feature navigation modules.
- [ ] screens live in feature presentation modules.
- [ ] each feature screen is registered with `IScreenProvider`.
- [ ] bottom-bar items are contributed by features, not centralized manually.
- [ ] app-level navigation does not need to know every screen implementation.
- [ ] generated features can compile and register screens.

## Repository references

- `core/navigation`
- `core/navigation/NavigationManager.kt`
- `core/navigation/ScreenRegistry.kt`
- `feature/*/navigation`
- `feature/*/presentation/*ScreenProvider.kt`
- `build-logic/convention/ScaffoldFeaturePlugin.kt`
