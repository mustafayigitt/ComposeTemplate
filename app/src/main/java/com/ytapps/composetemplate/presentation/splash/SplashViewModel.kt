package com.ytapps.composetemplate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.presentation.home.Home
import com.ytapps.composetemplate.presentation.login.Login
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val hasUser = true
            delay(1000)
            _uiState.value = SplashUiState(
                destinationRoute = if (hasUser) Home else Login,
                isLoading = false
            )
        }
    }
}