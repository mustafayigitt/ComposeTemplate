package com.ytapps.composetemplate.data.local

import android.content.Context
import androidx.core.content.edit
import com.ytapps.composetemplate.BuildConfig
import com.ytapps.composetemplate.data.model.AuthResponseModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
): IPreferencesManager {
    private val prefs =
        appContext.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    override fun hasUser(): Boolean {
        return getUUID() != null
    }

    override fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    override fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    override fun getTokenType(): String? {
        return prefs.getString(KEY_TOKEN_TYPE,null)
    }

    override fun getUUID(): String? {
        return prefs.getString(KEY_UUID, null)
    }

    override fun setAccessToken(accessToken: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
        }
    }

    override fun setRefreshToken(refreshToken: String) {
        prefs.edit {
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    override fun setTokenType(tokenType: String) {
        prefs.edit {
            putString(KEY_TOKEN_TYPE, tokenType)
        }
    }

    override fun setUUID(uuid: String) {
        prefs.edit {
            putString(KEY_UUID, uuid)
        }
    }

    override fun saveCredentials(authResponseModel: AuthResponseModel) {
        setAccessToken(authResponseModel.accessToken)
        setRefreshToken(authResponseModel.refreshToken)
        setTokenType(authResponseModel.tokenType)
    }

    override fun clear() {
        prefs.edit {
            clear()
        }
    }

    companion object {
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
        const val KEY_TOKEN_TYPE = "key_token_type"
        const val KEY_UUID = "key_uuid"
    }
}