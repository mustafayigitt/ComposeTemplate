package com.ytapps.composetemplate.core.analytics

import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.NavigationObserver
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Turns route changes into screen-view events.
 *
 * This used to be a `LaunchedEffect` inside `AppNavigation`, which forced `:app` to import
 * `IAnalyticsManager` and `AnalyticsEvent` and made `core:analytics` impossible to delete.
 */
@Singleton
class AnalyticsNavigationObserver
    @Inject
    constructor(
        private val analyticsManager: IAnalyticsManager,
    ) : NavigationObserver {
        override fun onRouteChanged(route: INavigationItem) {
            analyticsManager.logEvent(
                AnalyticsEvent(
                    type = AnalyticsEvent.SCREEN_VIEW,
                    extras = mapOf(AnalyticsEvent.SCREEN_NAME to route.route),
                ),
            )
        }
    }
