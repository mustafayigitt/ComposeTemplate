package com.ytapps.composetemplate.domain.usecase

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.domain.mapper.AuthMapper
import com.ytapps.composetemplate.domain.model.AuthModel
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */
class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val authMapper: AuthMapper
) {
    suspend operator fun invoke(authRequestModel: AuthRequestModel): Result<AuthModel> {
        val result = authRepository.login(authRequestModel)
        return if (result is Result.Success){
            Result.Success(authMapper.map(result.data))
        }else{
            Result.Error(result.error!!)
        }
    }
}