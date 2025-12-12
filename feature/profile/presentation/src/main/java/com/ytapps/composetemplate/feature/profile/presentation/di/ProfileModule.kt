package com.ytapps.composetemplate.feature.profile.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.profile.presentation.ProfileScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProfileModule {
    @Binds
    @IntoSet
    abstract fun bindProfileScreenProvider(
        provider: ProfileScreenProvider
    ): IScreenProvider
}
