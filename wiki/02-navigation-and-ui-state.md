# 02 - Navigation and UI State

## `core:navigation`

`INavigationItem`, `IBottomBarItem`, `INavigationManager`, `IScreenProvider`, `NavigationManager`, `ScreenRegistry`, and `NavigationObserver` form the navigation surface.

`NavigationManager` owns an in-memory `StateFlow` back stack and provides navigation, replacement, root, back, and tab operations. The stack is not persisted across process death.

`ScreenRegistry` receives `Set<IScreenProvider>` through Hilt multibinding. The first provider that claims a typed route renders it; unresolved routes currently render a fallback rather than failing fast.

## Optional observers

`NavigationObserver` is a multibinding contract. `core:analytics` contributes the current implementation while the app shell depends only on the contract. Removing analytics therefore leaves a valid empty set.

## App shell

`MainActivity` injects navigation contracts and renders `AppNavigation`. `AppNavigation`:

- renders Navigation3 `NavDisplay`
- notifies observers on route changes
- shows the bottom bar only for registered bottom-bar routes
- finishes the Activity when back navigation is unhandled

The app shell intentionally has no `NetworkMonitor` or global offline banner. Network-aware UI belongs in a feature that actually requires it, which keeps network-free generated consumers free of network concepts.

## Generated start flow

The generator projects one of two compile-time flows:

- `remote` and `offline-first`: incomplete onboarding → Onboarding; then stored user → Home, no user → Login. Logout returns to Login.
- `local` and `minimal`: incomplete onboarding → Onboarding; then Home. The auth feature and `LoginRoute` references are absent.

This is source projection at generation time, not runtime branching on internet availability.

## UI state

`BaseViewModel<S, E>` exposes immutable state and sends one-shot events through a channel. Feature screens follow the Route/UI split and collect state with lifecycle awareness.

The contract standardizes structure but does not replace product tests. Generated consumers should add ViewModel, navigation, restoration, and Compose UI coverage for their actual flows.

## `core:ui`

The design system includes buttons, fields, cards, dialogs, top bars, empty/error/loading states, search, image rendering, theme tokens, previews, and navigation-bar components. It does not contain a global no-internet banner.

---

[← Previous: 01 - Module Topology and Build System](01-module-topology.md) · [Index](README.md) · [Next: 03 - Network and Auth Token Flow →](03-network-and-auth.md)
