package com.ytapps.composetemplate.feature.auth.data.di

import com.ytapps.composetemplate.core.api.ITokenRefresher
import com.ytapps.composetemplate.feature.auth.data.AuthRepository
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
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
