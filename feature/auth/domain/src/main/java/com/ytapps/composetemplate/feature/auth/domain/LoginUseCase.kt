package com.ytapps.composetemplate.feature.auth.domain

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel
import javax.inject.Inject

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthModel> {
        return authRepository.login(email, password)
    }
}
