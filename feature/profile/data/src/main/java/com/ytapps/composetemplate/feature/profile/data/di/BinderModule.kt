package com.ytapps.composetemplate.feature.profile.data.di

import com.ytapps.composetemplate.feature.profile.data.ProfileRepository
import com.ytapps.composetemplate.feature.profile.domain.IProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BinderModule {
    @Binds
    @Singleton
    abstract fun bindProfileRepository(profileRepository: ProfileRepository): IProfileRepository
}
