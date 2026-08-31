package com.ytapps.composetemplate.core.navigation.di

import com.ytapps.composetemplate.core.navigation.NavigationObserver
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * Declares the observer set so it resolves to an empty set when nothing contributes to it.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface NavigationObserverModule {
    @Multibinds
    fun navigationObservers(): Set<NavigationObserver>
}
