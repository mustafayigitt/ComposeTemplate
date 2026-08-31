package com.ytapps.composetemplate.core.network.di

import com.ytapps.composetemplate.core.common.ITokenRefresher
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * Declares token refreshing as an optional capability.
 *
 * The network layer must build and serve requests even when no module provides token
 * refreshing, so the set is declared here as possibly empty. Without this declaration
 * Hilt fails the graph as soon as the contributing feature is removed, which is exactly
 * the plug-out case this template must support.
 *
 * At most one refresher is expected. The set is only a mechanism for "zero or one",
 * because Dagger's optional bindings would require `java.util.Optional`, which is not
 * available at this project's `minSdk`.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface TokenRefresherModule {
    @Multibinds
    fun tokenRefreshers(): Set<ITokenRefresher>
}
