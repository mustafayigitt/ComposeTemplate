package com.ytapps.composetemplate.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.ui.home.Home
import com.ytapps.composetemplate.ui.login.Login
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 25/08/2023
 * mustafa.yt65@gmail.com
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val hasUserUseCase: HasUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val hasUser = hasUserUseCase.invoke()
            delay(1000)
            _uiState.value = SplashUiState(
                destinationRoute = if (hasUser) Home.route else Login.route,
                isLoading = false
            )
        }
    }
}