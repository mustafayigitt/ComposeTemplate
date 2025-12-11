package com.ytapps.composetemplate.di

import com.ytapps.composetemplate.BuildConfig
import com.ytapps.composetemplate.contract.HomeRoute
import com.ytapps.composetemplate.contract.ProfileRoute
import com.ytapps.composetemplate.contract.SearchRoute
import com.ytapps.composetemplate.contract.SplashRoute
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.INavigationItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Provides
    @Singleton
    fun provideStartDestination(): INavigationItem {
        return SplashRoute
    }

    @Provides
    @IntoMap
    @StringKey("1")
    fun provideHomeBottomBarItem(): IBottomBarItem = HomeRoute

    @Provides
    @IntoMap
    @StringKey("2")
    fun provideSearchBottomBarItem(): IBottomBarItem = SearchRoute

    @Provides
    @IntoMap
    @StringKey("3")
    fun provideProfileBottomBarItem(): IBottomBarItem = ProfileRoute
}
