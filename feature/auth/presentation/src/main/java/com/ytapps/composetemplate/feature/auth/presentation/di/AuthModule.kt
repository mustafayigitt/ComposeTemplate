package com.ytapps.composetemplate.feature.auth.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.auth.presentation.AuthScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthModule {
    @Binds
    @IntoSet
    abstract fun bindAuthScreenProvider(
        provider: AuthScreenProvider
    ): IScreenProvider
}
