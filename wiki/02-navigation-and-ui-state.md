# 02 - Navigation and UI State

## `core:navigation`

`INavigationItem`, `IBottomBarItem`, `INavigationManager`, `IScreenProvider`, `NavigationManager`, `ScreenRegistry`, and `NavigationObserver` form the navigation surface.

`NavigationManager` owns an in-memory `StateFlow` back stack and provides `navigate`, `navigateOver`, `navigateToTop`, `navigateBack`, `navigateBackToRoot`, `selectTab`, and bottom-bar state. The back stack is not persisted across process death.

`ScreenRegistry` receives `Set<IScreenProvider>` through Hilt multibinding. The first provider that claims a typed route renders it; unresolved routes currently render a fallback message.

## Optional navigation observers

`NavigationObserver` is a multibinding contract. `core:analytics` contributes the current implementation, while `AppNavigation` knows only the contract. Deleting analytics therefore leaves an empty, valid set.

## `MainActivity` and `AppNavigation`

`MainActivity` injects only `INavigationManager`, `ScreenRegistry`, and `Set<NavigationObserver>`. It collects theme state and renders `AppNavigation`.

`AppNavigation`:

- renders Navigation3 `NavDisplay`
- notifies observers on route changes
- renders the bottom bar only for registered bottom-bar routes
- finishes the Activity when back navigation is unhandled

Connectivity monitoring and the global offline banner were deliberately removed. They were app-specific behavior living in always-retained modules, so a network-free generated project still contained network concepts. Network-aware screens may model connectivity in their own feature state when the product actually needs it.

## Generated start flow

The generator projects one of two compile-time flows:

- `remote` and `offline-first`: Onboarding → Login when no user is stored; otherwise Home. Logout returns to Login.
- `local` and `minimal`: Onboarding → Home. The complete auth feature is absent.

This is generation-time source projection, not a runtime check for internet availability. A network-free output never references `LoginRoute`.

## UI state

`BaseViewModel<S, E>` exposes immutable state and sends one-shot events through an unbuffered channel. Feature screens follow the Route/UI split and collect state with lifecycle awareness.

## `core:ui`

The design system includes buttons, fields, cards, dialogs, top bars, empty/error/loading states, search, image rendering, theme tokens, previews, and navigation-bar components. It no longer contains a global no-internet banner.

---

[← Previous: 01 - Module Topology and Build System](01-module-topology.md) · [Index](README.md) · [Next: 03 - Network and Auth Token Flow →](03-network-and-auth.md)
