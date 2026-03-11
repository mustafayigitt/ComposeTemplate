package com.lhacenmed.budget.feature.auth.data.di

import com.lhacenmed.budget.core.api.ITokenRefresher
import com.lhacenmed.budget.feature.auth.data.AuthRepository
import com.lhacenmed.budget.feature.auth.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BinderModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): IAuthRepository

    @Binds
    abstract fun bindTokenRefresher(
        authRepository: AuthRepository
    ): ITokenRefresher
}
