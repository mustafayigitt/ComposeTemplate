package com.ytapps.composetemplate.domain.usecase

import com.ytapps.composetemplate.domain.repository.IAuthRepository
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

class HasUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.hasUser()
    }
}