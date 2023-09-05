package com.ytapps.composetemplate.core.di

import com.ytapps.composetemplate.data.local.IPreferencesManager
import com.ytapps.composetemplate.data.local.PreferencesManager
import com.ytapps.composetemplate.data.repository.AuthRepository
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BinderModule {
    @Binds
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): IAuthRepository


    @Binds
    abstract fun bindPreferencesManager(
        preferencesManager: PreferencesManager
    ): IPreferencesManager
}