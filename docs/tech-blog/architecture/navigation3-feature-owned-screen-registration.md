# Navigation3 with Feature-Owned Screen Registration

Navigation becomes hard to scale when every route and screen is registered in one central place. ComposeTemplate uses Navigation3 with feature-owned routes and screen providers.

## Problem

Centralized navigation graphs create coupling. Every new screen requires app-level edits, route strings spread across the project, and bottom-bar registration becomes a shared mutable list.

## ComposeTemplate approach

The navigation layer is built around:

- `INavigationItem`
- `INavigationManager`
- `ScreenRegistry`
- `IScreenProvider`
- `IBottomBarItem`

Routes are data. Features define their own route objects and presentation modules contribute screen providers through Hilt multibinding.

## Takeaway

Navigation is an architectural boundary. Treating it as feature-owned keeps modular Compose projects scalable.
