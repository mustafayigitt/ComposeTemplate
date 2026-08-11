package com.ytapps.composetemplate.core.analytics

/**
 * Interface for managing analytics tracking across the application.
 */
interface IAnalyticsManager {
    /**
     * Tracks a specific analytics event.
     */
    fun logEvent(event: AnalyticsEvent)

    /**
     * Sets a user property.
     */
    fun setUserProperty(
        name: String,
        value: String,
    )

    /**
     * Sets the user ID.
     */
    fun setUserId(userId: String?)
}
