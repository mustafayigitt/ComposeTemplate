package com.ytapps.composetemplate.core.data.di

import com.ytapps.composetemplate.core.data.security.KeystoreTokenStore
import com.ytapps.composetemplate.core.data.security.TokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecurityModule {
    @Binds
    abstract fun bindTokenStore(tokenStore: KeystoreTokenStore): TokenStore
}
