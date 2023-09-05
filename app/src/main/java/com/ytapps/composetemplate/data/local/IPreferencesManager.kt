package com.ytapps.composetemplate.data.local

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

interface IPreferencesManager {
    fun hasUser(): Boolean
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getTokenType(): String?
    fun getUUID(): String?
    fun setAccessToken(accessToken: String)
    fun setRefreshToken(refreshToken: String)
    fun setTokenType(tokenType: String)
    fun setUUID(uuid: String)
    fun clear()
}