package com.ytapps.composetemplate.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import com.ytapps.composetemplate.feature.splash.domain.GetStartDestinationUseCase
import com.ytapps.composetemplate.feature.splash.domain.SplashDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel
    @Inject
    constructor(
        private val getStartDestinationUseCase: GetStartDestinationUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SplashUiState())
        val uiState = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                val destination = getStartDestinationUseCase()
                delay(1000L)
                _uiState.value =
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
