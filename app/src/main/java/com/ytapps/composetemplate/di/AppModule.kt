package com.ytapps.composetemplate.di

import com.ytapps.composetemplate.BuildConfig
import com.ytapps.composetemplate.contract.HomeRoute
import com.ytapps.composetemplate.contract.ProfileRoute
import com.ytapps.composetemplate.contract.SearchRoute
import com.ytapps.composetemplate.contract.SplashRoute
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideNavigationManager(): NavigationManager {
        val startDestination = SplashRoute
        val bottomBarItems = listOf<IBottomBarItem>(
            HomeRoute,
            SearchRoute,
            ProfileRoute
        )
        return NavigationManager(startDestination, bottomBarItems)
    }
}
