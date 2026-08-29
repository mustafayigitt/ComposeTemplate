# 02 - Navigation and UI State

## `core:navigation` surface

`INavigationItem`, `IBottomBarItem`, `INavigationManager`, `IScreenProvider`, `NavigationManager`, `ScreenRegistry`, `di/NavigationModule.kt`.

## `NavigationManager` (internal, `@Singleton`)

Injected dependencies:

- `startDestination: INavigationItem`
- `Map<String, IBottomBarItem>` (Hilt multibinding, **sorted by map key**)
- `IPreferencesManager`

State and operations:

- Back stack is a `MutableStateFlow<List<INavigationItem>>` — a plain in-memory list, not `SavedStateHandle`-backed.
- `navigate`, `navigateOver`, `navigateToTop`, `navigateBack`, `navigateBackToRoot`, `selectTab`.
- Also exposes `isDarkModeFlow`, delegated from preferences.

> Two consequences worth documenting for consumers: (1) bottom-bar ordering depends on multibinding **key strings**, so keys act as an ordering convention; (2) theme state is reachable through the navigation contract, which is why `MainActivity` reads dark mode from `INavigationManager`.

## `ScreenRegistry` (`@Singleton`)

- Injected `Set<IScreenProvider>` (multibinding).
- Iterates providers linearly; the **first** provider that claims a route wins.
- If no provider claims the route, it renders a fallback composable: `"Screen not found: ${route.route}"` instead of throwing.

This makes a misconfigured feature a silent visual defect rather than a crash, and `Set` iteration order is not guaranteed if two providers claim the same route.

## `MainActivity` and `AppNavigation`

- `@AndroidEntryPoint`; field-injects `INavigationManager`, `ScreenRegistry`, `NetworkMonitor`, `IAnalyticsManager`, `LocaleManager`.
- `enableEdgeToEdge()`.
- Dark mode collected from `navigationManager.isDarkModeFlow` via `collectAsStateWithLifecycle`.
- `LaunchedEffect` calls `localeManager.restoreSavedLanguage()`.
- Renders `ComposeTemplateTheme { AppNavigation(...) }`.
- `AppNavigation` uses Navigation3 `NavDisplay`, logs a screen-view analytics event on route change, shows an offline banner above content, and shows the bottom bar only when the current route is a bottom-bar item. Unhandled back finishes the Activity.

## UI state contract: `BaseViewModel<S, E>`

```kotlin
abstract class BaseViewModel<S, E> : ViewModel() {
    protected abstract val uiStateInternal: MutableStateFlow<S>
    val uiState: StateFlow<S> by lazy { uiStateInternal.asStateFlow() }
    // one-shot events via Channel + receiveAsFlow
}
```

- `updateState { ... }` for reducer-style state changes.
- `sendEvent(...)` for one-shot events (navigation, snackbar) through an **unbuffered** `Channel` consumed with `receiveAsFlow` — single-consumer semantics by design.

## `core:ui` design system

- ~19 `App*` components: `AppButton`, `AppTextField`, `AppCard`, `AppDialog`, `AppTopBar`, `AppEmptyState`, `AppErrorState`, `AppSkeleton`, `AppNoInternetBanner`, `AppSearchField`, `AppAsyncImage`, and more.
- `theme/` with `Color`, `Type`, `Spacing`, `Theme`, plus `component/AppNavigationBar`.
- `ShimmerModifier`, `PreviewAnnotations`, and `DesignSystemScreen` — an in-app catalog of the design system.

---

[← Previous: 01 - Module Topology and Build System](01-module-topology.md) · [Index](README.md) · [Next: 03 - Network and Auth Token Flow →](03-network-and-auth.md)
