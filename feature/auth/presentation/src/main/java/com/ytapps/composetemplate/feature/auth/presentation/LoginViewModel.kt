package com.ytapps.composetemplate.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.common.onError
import com.ytapps.composetemplate.core.common.onSuccess
import com.ytapps.composetemplate.core.ui.BaseViewModel
import com.ytapps.composetemplate.core.ui.CommonUiEvent
import com.ytapps.composetemplate.feature.auth.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel
    @Inject
    constructor(
        private val login: LoginUseCase,
    ) : BaseViewModel<LoginUiState, CommonUiEvent>(LoginUiState()) {

        fun login(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                updateState { LoginUiState(isLoading = true) }

                login
                    .invoke(email, password)
                    .onSuccess {
                        updateState {
                            LoginUiState(
                                shouldNavigateToSplash = true,
                                isLoading = false,
                            )
                        }
                    }.onError { message, _ ->
                        updateState { LoginUiState(isLoading = false) }
                        sendEvent(CommonUiEvent.ShowSnackbar(message))
                    }
            }
        }
    }