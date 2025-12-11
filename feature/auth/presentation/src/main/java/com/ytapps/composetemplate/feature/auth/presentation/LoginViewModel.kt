package com.ytapps.composetemplate.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.api.onError
import com.ytapps.composetemplate.core.api.onSuccess
import com.ytapps.composetemplate.feature.auth.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            
            loginUseCase.invoke(email, password)
                .onSuccess {
                    _uiState.value = LoginUiState(
                        shouldNavigateToSplash = true,
                        isLoading = false
                    )
                }
                .onError { message, _ ->
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        error = message
                    )
                }
        }
    }
}