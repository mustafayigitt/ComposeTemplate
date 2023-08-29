package com.ytapps.composetemplate.ui.login

import com.ytapps.composetemplate.core.base.Result
import com.ytapps.composetemplate.data.model.AuthModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.data.repository.IAuthRepository
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