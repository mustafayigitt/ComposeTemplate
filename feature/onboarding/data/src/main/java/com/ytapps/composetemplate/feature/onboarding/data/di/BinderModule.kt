package com.ytapps.composetemplate.feature.onboarding.data.di

import com.ytapps.composetemplate.feature.onboarding.data.OnboardingRepository
import com.ytapps.composetemplate.feature.onboarding.domain.IOnboardingRepository
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
    abstract fun bindOnboardingRepository(onboardingRepository: OnboardingRepository): IOnboardingRepository
}
