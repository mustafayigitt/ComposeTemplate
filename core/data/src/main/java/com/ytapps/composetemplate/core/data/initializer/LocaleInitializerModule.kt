package com.ytapps.composetemplate.core.data.initializer

import com.ytapps.composetemplate.core.common.initializer.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal interface LocaleInitializerModule {
    @Binds
    @IntoSet
    fun bindLocaleInitializer(initializer: LocaleInitializer): AppInitializer
}
