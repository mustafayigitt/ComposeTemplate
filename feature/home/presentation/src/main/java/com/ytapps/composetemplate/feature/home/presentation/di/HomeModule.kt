package com.ytapps.composetemplate.feature.home.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.home.presentation.HomeScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class HomeModule {
    @Binds
    @IntoSet
    abstract fun bindHomeScreenProvider(
        provider: HomeScreenProvider
    ): IScreenProvider
}
