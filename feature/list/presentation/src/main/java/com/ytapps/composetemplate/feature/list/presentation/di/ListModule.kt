package com.ytapps.composetemplate.feature.list.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.list.presentation.ListScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ListModule {
    @Binds
    @IntoSet
    abstract fun bindListScreenProvider(
        provider: ListScreenProvider
    ): IScreenProvider
}
