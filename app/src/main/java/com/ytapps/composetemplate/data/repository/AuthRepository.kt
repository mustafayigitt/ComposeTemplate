package com.ytapps.composetemplate.data.repository

import com.ytapps.composetemplate.core.base.BaseRepository
import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.data.remote.AuthService
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.data.local.IPreferencesManager
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val prefs: IPreferencesManager
): BaseRepository(), IAuthRepository {
    override fun hasUser(): Boolean {
        return prefs.hasUser()
    }

    override suspend fun login(authRequestModel: AuthRequestModel): Result<AuthResponseModel> {
        val result = safeCall {
            authService.login(authRequestModel)
        }
        if (result is Result.Success) {
            prefs.saveCredentials(result.data)
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