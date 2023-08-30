package com.ytapps.composetemplate.domain.usecase

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.data.model.AuthModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */
class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(authRequestModel: AuthRequestModel): Result<AuthModel> {
        return authRepository.login(authRequestModel)
    }
}