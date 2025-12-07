package com.ytapps.composetemplate.feature.auth.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
internal data class AuthRequestModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)