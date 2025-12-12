package com.ytapps.composetemplate.feature.detail.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.detail.presentation.DetailScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DetailModule {
    @Binds
    @IntoSet
    abstract fun bindDetailScreenProvider(
        provider: DetailScreenProvider
    ): IScreenProvider
}
