package com.ytapps.composetemplate.data.repository

import com.ytapps.composetemplate.data.model.AuthModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import com.ytapps.composetemplate.core.base.Result

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

interface IAuthRepository {
    fun hasUser(): Boolean

    suspend fun login(authRequestModel: AuthRequestModel): Result<AuthModel>
}