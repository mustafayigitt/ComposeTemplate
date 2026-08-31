package com.ytapps.composetemplate.core.common.initializer

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * Declares the initializer multibinding as possibly empty.
 *
 * Without this declaration Hilt fails to build the graph when every contributing module
 * has been deleted, which is exactly the plug-out case this architecture must support.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface AppInitializerModule {
    @Multibinds
    fun appInitializers(): Set<AppInitializer>
}
