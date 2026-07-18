package com.ytapps.composetemplate.feature.splash.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.ui.BaseViewModel
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import com.ytapps.composetemplate.feature.onboarding.navigation.OnboardingRoute
import com.ytapps.composetemplate.feature.splash.domain.GetStartDestinationUseCase
import com.ytapps.composetemplate.feature.splash.domain.SplashDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel
    @Inject
    constructor(
        private val getStartDestinationUseCase: GetStartDestinationUseCase,
    ) : BaseViewModel<SplashUiState, SplashEvent>() {
        override val uiStateInternal = MutableStateFlow(SplashUiState())

        fun checkDestination() {
            updateState { SplashUiState(isLoading = true) }
            viewModelScope.launch {
                val destination = getStartDestinationUseCase()
                delay(SPLASH_DELAY)
                val route: INavigationItem =
                    when (destination) {
                        SplashDestination.Onboarding -> OnboardingRoute
                        SplashDestination.Home -> HomeRoute
                        SplashDestination.Login -> LoginRoute
                    }
                updateState { SplashUiState(isLoading = false) }
                sendEvent(SplashEvent.NavigateTo(route))
            }
        }

        private companion object {
            private const val SPLASH_DELAY = 1000L
        }
    }
