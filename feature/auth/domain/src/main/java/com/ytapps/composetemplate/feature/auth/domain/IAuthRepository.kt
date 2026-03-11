package com.lhacenmed.budget.feature.auth.domain

import com.lhacenmed.budget.core.api.Result
import com.lhacenmed.budget.core.api.ITokenRefresher
import com.lhacenmed.budget.feature.auth.domain.model.AuthModel

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */

interface IAuthRepository : ITokenRefresher {
    fun hasUser(): Boolean
    suspend fun login(email: String, password: String): Result<AuthModel>
}
