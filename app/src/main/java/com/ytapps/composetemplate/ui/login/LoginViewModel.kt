package com.ytapps.composetemplate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.base.Result
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
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
            val authRequestModel = AuthRequestModel(email, password)
            val result = loginUseCase.invoke(authRequestModel)
            if (result is Result.Success) {
                _uiState.value = LoginUiState(
                    shouldNavigateToSplash = true,
                    isLoading = false
                )
            } else {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    error = result.error
                )
            }
        }
    }
}