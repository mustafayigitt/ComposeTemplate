package com.ytapps.composetemplate.core.common

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal object DispatcherModule {
    @IoDispatcher
    @Provides
    @Singleton
    fun provideIoDispatcherScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
