package com.ytapps.composetemplate.feature.auth.presentation

import com.ytapps.composetemplate.core.base.BaseUiState

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
data class LoginUiState(
    val shouldNavigateToSplash: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: String? = null
) : BaseUiState(isLoading, error) {

    var email: String = ""
    var password: String = ""
}