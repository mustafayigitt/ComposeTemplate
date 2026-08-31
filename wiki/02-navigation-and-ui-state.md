# 02 - Navigation and UI State

## `core:navigation` surface

`INavigationItem`, `IBottomBarItem`, `INavigationManager`, `IScreenProvider`, `NavigationManager`, `ScreenRegistry`, `NavigationObserver`, `di/NavigationModule.kt`, `di/NavigationObserverModule.kt`.

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

## `NavigationObserver`

```kotlin
interface NavigationObserver {
    fun onRouteChanged(route: INavigationItem)
}
```

- Declared with `@Multibinds` in `NavigationObserverModule`, so the set resolves even when **nothing** contributes to it.
- `core:analytics` contributes `AnalyticsNavigationObserver` with `@Binds @IntoSet`; it is the only contributor today.
- The navigation host iterates the set on every route change and knows none of the implementations.

This is the mechanism that lets `core:analytics` be deleted from a generated project. Screen-view logging used to be a `LaunchedEffect` inside `AppNavigation`, which forced `:app` to import `IAnalyticsManager` and `AnalyticsEvent` and made the module impossible to remove.

## `MainActivity` and `AppNavigation`

- `@AndroidEntryPoint`; field-injects `INavigationManager`, `ScreenRegistry`, `NetworkMonitor` (from `core:common`) and `Set<NavigationObserver>`. **Nothing here comes from an optional module.**
- `enableEdgeToEdge()`.
- Dark mode collected from `navigationManager.isDarkModeFlow` via `collectAsStateWithLifecycle`.
- Renders `ComposeTemplateTheme { AppNavigation(...) }`.
- `AppNavigation` uses Navigation3 `NavDisplay`, notifies every `NavigationObserver` on route change, shows an offline banner above content, and shows the bottom bar only when the current route is a bottom-bar item. Unhandled back finishes the Activity.
- Language restoration is **not** done here. It runs as a `LocaleInitializer` contributed to `Set<AppInitializer>` from `core:data` (see page 01).

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
