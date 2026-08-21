# Navigation Architecture

Navigation in ComposeTemplate is feature-owned and type-safe.

## Building blocks

- `INavigationItem`
- `IBottomBarItem`
- `INavigationManager`
- `ScreenRegistry`
- `IScreenProvider`

## Approach

Routes are modeled as type-safe Kotlin objects or data classes. Each feature registers its own screen through `IScreenProvider`. `ScreenRegistry` resolves routes from a Hilt multibound set of providers.

## Bottom bar

Bottom-bar items are contributed from feature navigation modules via Hilt map multibinding. This avoids a central tab list.

## Checklist

- [ ] Route is owned by navigation.
- [ ] ScreenProvider is owned by presentation.
- [ ] App module avoids feature screen details.
