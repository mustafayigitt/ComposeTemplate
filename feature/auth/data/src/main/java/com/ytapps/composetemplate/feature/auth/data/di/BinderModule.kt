package com.ytapps.composetemplate.feature.auth.data.di

import com.ytapps.composetemplate.core.common.ITokenRefresher
import com.ytapps.composetemplate.feature.auth.data.AuthRepository
import com.ytapps.composetemplate.feature.auth.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BinderModule {
    @Binds
    abstract fun bindAuthRepository(authRepository: AuthRepository): IAuthRepository

    /**
     * Contributed into a set so the network layer keeps working when this feature is
     * removed. Nothing outside this module needs to know who refreshes tokens.
     */
    @Binds
    @IntoSet
    abstract fun bindTokenRefresher(authRepository: AuthRepository): ITokenRefresher
}
