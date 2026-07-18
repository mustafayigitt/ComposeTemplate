package com.ytapps.composetemplate.core.analytics

/**
 * Base class for analytics events.
 */
data class AnalyticsEvent(
    val type: String,
    val extras: Map<String, Any?> = emptyMap(),
) {
    companion object {
        const val SCREEN_VIEW = "screen_view"
        const val BUTTON_CLICK = "button_click"

        // Keys
        const val SCREEN_NAME = "screen_name"
        const val BUTTON_NAME = "button_name"
    }
}
