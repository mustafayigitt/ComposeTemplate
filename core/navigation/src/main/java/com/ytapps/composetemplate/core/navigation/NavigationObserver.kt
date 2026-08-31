package com.ytapps.composetemplate.core.navigation

/**
 * Reacts to navigation changes without the navigation host knowing who is listening.
 *
 * Implementations are contributed with `@IntoSet` from their own module. The set is declared with
 * `@Multibinds`, so it may legally be empty: deleting every contributing module leaves navigation
 * working and silent. This is how `core:analytics` records screen views without `:app` ever
 * importing an analytics symbol.
 */
interface NavigationObserver {
    fun onRouteChanged(route: INavigationItem)
}
