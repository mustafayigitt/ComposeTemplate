package com.ytapps.composetemplate.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.contract.HomeRoute
import com.ytapps.composetemplate.contract.LoginRoute
import com.ytapps.composetemplate.core.local.IPreferencesManager
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
internal class SplashViewModel @Inject constructor(
    //private val preferencesManager: IPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            //val hasUser = preferencesManager.hasUser()
            val hasUser = true
            delay(1000)
            _uiState.value = SplashUiState(
                destinationRoute = if (hasUser) HomeRoute else LoginRoute,
                isLoading = false
            )
        }
    }
}