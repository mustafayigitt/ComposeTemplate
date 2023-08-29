package com.ytapps.composetemplate.core.base

import com.ytapps.composetemplate.util.Constants
import retrofit2.Response

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */
abstract class BaseRepository {
    suspend fun <T : Any> safeCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.errorBody()?.string() ?: Constants.DEFAULT_ERROR)
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }
}