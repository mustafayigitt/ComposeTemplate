package com.lhacenmed.budget.feature.search.presentation.di

import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.search.presentation.SearchScreenProvider
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
