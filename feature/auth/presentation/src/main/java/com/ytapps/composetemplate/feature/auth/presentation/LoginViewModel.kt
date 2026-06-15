package com.ytapps.composetemplate.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.common.onError
import com.ytapps.composetemplate.core.common.onSuccess
import com.ytapps.composetemplate.core.ui.BaseViewModel
import com.ytapps.composetemplate.feature.auth.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
) : BaseViewModel<LoginUiState, LoginEvent>() {
    override val _uiState = MutableStateFlow(LoginUiState())

    fun onEmailChanged(email: String) {
        updateState { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        updateState { it.copy(password = password) }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            login.invoke(email, password)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }
                    sendEvent(LoginEvent.NavigateToSplash)
                }.onError { message, _ ->
                    updateState { it.copy(isLoading = false) }
                    sendEvent(LoginEvent.ShowSnackbar(message))
                }
        }
    }
}
