package com.ytapps.composetemplate.core.analytics.di

import com.ytapps.composetemplate.core.analytics.AnalyticsNavigationObserver
import com.ytapps.composetemplate.core.analytics.IAnalyticsManager
import com.ytapps.composetemplate.core.analytics.TimberAnalyticsTracker
import com.ytapps.composetemplate.core.navigation.NavigationObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsManager(timberAnalyticsTracker: TimberAnalyticsTracker): IAnalyticsManager

    @Binds
    @IntoSet
    abstract fun bindAnalyticsNavigationObserver(observer: AnalyticsNavigationObserver): NavigationObserver
}
