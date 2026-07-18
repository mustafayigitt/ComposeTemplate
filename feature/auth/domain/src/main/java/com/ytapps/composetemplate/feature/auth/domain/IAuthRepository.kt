package com.ytapps.composetemplate.feature.auth.domain

import com.ytapps.composetemplate.core.common.Result
import com.ytapps.composetemplate.core.network.ITokenRefresher
import com.ytapps.composetemplate.feature.auth.domain.model.AuthModel

interface IAuthRepository : ITokenRefresher {
    fun hasUser(): Boolean

    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthModel>
}
