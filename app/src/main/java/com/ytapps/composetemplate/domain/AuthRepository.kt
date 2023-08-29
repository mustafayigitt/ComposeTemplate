package com.ytapps.composetemplate.domain

import com.ytapps.composetemplate.core.base.BaseRepository
import com.ytapps.composetemplate.data.local.PreferencesManager
import com.ytapps.composetemplate.data.model.AuthModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.data.remote.AuthService
import com.ytapps.composetemplate.data.repository.IAuthRepository
import com.ytapps.composetemplate.core.base.Result
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val prefs: PreferencesManager
): BaseRepository(), IAuthRepository {
    override fun hasUser(): Boolean {
        return prefs.hasUser()
    }

    override suspend fun login(authRequestModel: AuthRequestModel): Result<AuthModel> {
        return safeCall {
            authService.login(authRequestModel)
        }
    }
}