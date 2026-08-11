package com.ytapps.composetemplate.core.analytics

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimberAnalyticsTracker
    @Inject
    constructor() : IAnalyticsManager {
        override fun logEvent(event: AnalyticsEvent) {
            Timber.tag("Analytics").d("Event: ${event.type}, Extras: ${event.extras}")
        }

        override fun setUserProperty(
            name: String,
            value: String,
        ) {
            Timber.tag("Analytics").d("UserProperty: $name = $value")
        }

        override fun setUserId(userId: String?) {
            Timber.tag("Analytics").d("UserId: $userId")
        }
    }
