package com.ytapps.composetemplate.core.base

import com.ytapps.composetemplate.core.api.Result
import com.ytapps.composetemplate.core.util.Constants
import retrofit2.Response

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
abstract class BaseRepository {
    
    protected suspend fun <T : Any> safeCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    message = response.errorBody()?.string() ?: Constants.DEFAULT_ERROR
                )
            }
        } catch (e: Exception) {
            Result.Error(
                message = e.message ?: Constants.DEFAULT_ERROR,
                throwable = e
            )
        }
    }
}