package com.ytapps.composetemplate.data.local

import com.ytapps.composetemplate.data.model.AuthResponseModel
import javax.inject.Inject

/**
 * Created by mustafayigitt on 05/09/2023
 * mustafa.yt65@gmail.com
 */
class MockPreferencesManager @Inject constructor() : IPreferencesManager {

    private val prefs = HashMap<String, String>()

    override fun hasUser(): Boolean {
        return getUUID() != null
    }

    override fun getAccessToken(): String? {
        return prefs[PreferencesManager.KEY_ACCESS_TOKEN]
    }

    override fun getRefreshToken(): String? {
        return prefs[PreferencesManager.KEY_REFRESH_TOKEN]
    }

    override fun getTokenType(): String? {
        return prefs[PreferencesManager.KEY_TOKEN_TYPE]
    }

    override fun getUUID(): String? {
        return prefs[PreferencesManager.KEY_UUID]
    }

    override fun setAccessToken(accessToken: String) {
        prefs[PreferencesManager.KEY_ACCESS_TOKEN] = accessToken
    }

    override fun setRefreshToken(refreshToken: String) {
        prefs[PreferencesManager.KEY_REFRESH_TOKEN] = refreshToken
    }

    override fun setTokenType(tokenType: String) {
        prefs[PreferencesManager.KEY_TOKEN_TYPE] = tokenType
    }

    override fun setUUID(uuid: String) {
        prefs[PreferencesManager.KEY_UUID] = uuid
    }

    override fun saveCredentials(authResponseModel: AuthResponseModel) {
        setAccessToken(authResponseModel.accessToken)
        setRefreshToken(authResponseModel.refreshToken)
        setTokenType(authResponseModel.tokenType)
    }

    override fun clear() {
        prefs.clear()
    }
}