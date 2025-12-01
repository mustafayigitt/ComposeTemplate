package com.ytapps.composetemplate.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
data class AuthRequestModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)