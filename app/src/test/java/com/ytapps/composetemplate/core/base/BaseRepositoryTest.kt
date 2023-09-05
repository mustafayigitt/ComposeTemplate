package com.ytapps.composetemplate.core.base

import com.ytapps.composetemplate.core.api.Result
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Created by mustafa.yigit on 05/09/2023
 * mustafa.yt65@gmail.com
 */
class BaseRepositoryTest {

    private lateinit var baseRepository: BaseRepository


    @Before
    fun setUp() {
        baseRepository = object : BaseRepository() {}
    }

    @Test
    fun `given success response when safeCall then it should return Result-Success`() {
        // given
        val data = "success"
        val response = Response.success(data)

        // when
        val result = runBlocking { baseRepository.safeCall { response } }

        // then
        assert(result is Result.Success)
    }

    @Test
    fun `given error response when safeCall then it should return Result-Error`() {
        // given
        val error = "error"
        val response = Response.error<String>(
            400,
            error.toResponseBody("application/json".toMediaType())
        )

        // when
        val result = runBlocking { baseRepository.safeCall { response } }

        // then
        assert(result is Result.Error)
    }

    @Test
    fun `given throw exception when safeCall then it should return Result-Error`() {
        // given
        val exception = Exception("exception")

        // when
        val result = runBlocking { baseRepository.safeCall<String> { throw exception } }

        // then
        assert(result is Result.Error)
    }
}