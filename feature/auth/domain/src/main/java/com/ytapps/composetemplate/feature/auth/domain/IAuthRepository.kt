package com.ytapps.composetemplate.feature.auth.domain

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.core.api.ITokenRefresher
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */

interface IAuthRepository : ITokenRefresher {
    fun hasUser(): Boolean
    suspend fun login(email: String, password: String): Result<AuthModel>
}
