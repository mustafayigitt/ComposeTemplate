package com.ytapps.composetemplate.feature.splash.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import com.ytapps.composetemplate.feature.splash.domain.GetStartDestinationUseCase
import com.ytapps.composetemplate.feature.splash.domain.SplashDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel
    @Inject
    constructor(
        private val getStartDestinationUseCase: GetStartDestinationUseCase,
    ) : BaseViewModel<SplashUiState, Unit>(SplashUiState()) {

        init {
            viewModelScope.launch {
                val destination = getStartDestinationUseCase()
                delay(SPLASH_DELAY)
                updateState {
                    SplashUiState(
                        destinationRoute =
                            when (destination) {
                                SplashDestination.Home -> HomeRoute
                                SplashDestination.Login -> LoginRoute
                            },
                        isLoading = false,
                    )
                }
            }
        }

        private companion object {
            private const val SPLASH_DELAY = 1000L
        }
    }