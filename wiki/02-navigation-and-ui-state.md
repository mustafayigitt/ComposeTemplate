# 02 - Navigation and UI State

## Navigation contract

`NavigationManager` owns the typed Navigation3 back stack. `MainActivity` persists stable route strings in `onSaveInstanceState` and restores them through `INavigationManager.restoreBackStack` and `IScreenProvider.restoreRoute`. Missing features are skipped, and the start destination is prepended when restored history does not contain it.

Each retained feature provider restores its object route; detail restores its typed ID route. `scaffoldFeature` emits the same restoration hook for newly generated features.

`ScreenRegistry` rejects multiple restorers for one route and throws when no provider can render a route. Unknown routes therefore fail fast instead of silently showing a fallback.

## App shell and theme

`MainActivity` injects `IThemeManager` separately from `INavigationManager`. Navigation no longer owns preference or theme concerns. Optional navigation observers remain multibound, allowing analytics to be removed safely.

## Start flow

- `remote` / `offline-first`: incomplete onboarding → Onboarding; then stored user → Home, no user → Login.
- `local` / `minimal`: incomplete onboarding → Onboarding; then Home, with no auth or `LoginRoute` residue.

## Coverage

Focused tests cover back-stack restoration, route reconstruction, duplicate restorers, unknown routes, and generated scaffold compilation. Product consumers should extend these with feature-specific ViewModel and Compose UI tests.

---

[← Previous](01-module-topology.md) · [Index](README.md) · [Next →](03-network-and-auth.md)
