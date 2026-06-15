package com.ytapps.composetemplate.feature.auth.domain

import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<AuthModel> = authRepository.login(email, password)
}
