package com.lhacenmed.budget.feature.list.presentation.di

import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.list.presentation.ListScreenProvider
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
