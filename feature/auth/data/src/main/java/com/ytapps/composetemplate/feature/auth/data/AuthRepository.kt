package com.lhacenmed.budget.feature.auth.data

import com.lhacenmed.budget.core.api.Result
import com.lhacenmed.budget.core.api.map
import com.lhacenmed.budget.core.base.BaseRepository
import com.lhacenmed.budget.core.local.IPreferencesManager
import com.lhacenmed.budget.feature.auth.data.model.AuthRequestModel
import com.lhacenmed.budget.feature.auth.data.remote.AuthService
import com.lhacenmed.budget.feature.auth.domain.IAuthRepository
import com.lhacenmed.budget.feature.auth.domain.model.AuthModel
import retrofit2.Response
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val prefs: IPreferencesManager
) : BaseRepository(), IAuthRepository {

    override fun hasUser(): Boolean {
        return prefs.hasUser()
    }

    override suspend fun login(email: String, password: String): Result<AuthModel> {
        val result = safeCall(
            call = {
                val requestModel = AuthRequestModel(
                    email = email,
                    password = password
                )
                authService.login(requestModel)
            }
        ).map {
            AuthModel(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
                expiresIn = it.expiresIn,
                tokenType = it.tokenType
            )
        }
        if (result is Result.Success) {
            val data = result.data
            prefs.setAccessToken(data.accessToken)
            prefs.setRefreshToken(data.refreshToken)
            prefs.setTokenType(data.tokenType)
        }
        return result
    }

    override suspend fun refreshToken(): Result<String> {
        return safeCall {
            //TODO: Not Implemented
            // authService.refreshToken()
            Response.success("")
        }
    }
}
