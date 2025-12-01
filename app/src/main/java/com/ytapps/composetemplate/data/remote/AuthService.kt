package com.ytapps.composetemplate.data.remote

import com.ytapps.composetemplate.data.model.AuthResponseModel
import com.ytapps.composetemplate.data.model.AuthRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
interface AuthService {
    @GET("/auth/login")
    suspend fun login(
        @Body authRequestModel: AuthRequestModel
    ): Response<AuthResponseModel>
}