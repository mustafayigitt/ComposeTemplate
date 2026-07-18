package com.ytapps.composetemplate.feature.onboarding.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.onboarding.presentation.OnboardingScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {
    @Binds
    @IntoSet
    abstract fun bindOnboardingScreenProvider(onboardingScreenProvider: OnboardingScreenProvider): IScreenProvider
}
