package com.ytapps.composetemplate.core.api

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
sealed class Result<T : Any>(
    open val data: T?,
    open val error: String?
) {
    data class Error<T : Any>(override val error: String) : Result<T>(null, error)
    data class Success<T : Any>(override val data: T) : Result<T>(data, null)
}
