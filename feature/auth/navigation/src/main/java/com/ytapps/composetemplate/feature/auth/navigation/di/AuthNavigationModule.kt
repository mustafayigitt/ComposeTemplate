package com.ytapps.composetemplate.feature.auth.navigation.di

import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal object AuthNavigationModule {
    @Provides
    @IntoSet
    fun provideLoginRoute(): INavigationItem = LoginRoute
}
