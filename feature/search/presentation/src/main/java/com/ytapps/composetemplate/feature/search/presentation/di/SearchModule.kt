package com.ytapps.composetemplate.feature.search.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.search.presentation.SearchScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SearchModule {
    @Binds
    @IntoSet
    abstract fun bindSearchScreenProvider(
        provider: SearchScreenProvider
    ): IScreenProvider
}
