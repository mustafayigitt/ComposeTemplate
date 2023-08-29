package com.ytapps.composetemplate.data.local

import android.content.Context
import com.ytapps.composetemplate.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */
class PreferencesManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    private val prefs =
        appContext.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    fun hasUser(): Boolean {
        return getUUID() != null
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getTokenType(): String? {
        return prefs.getString(KEY_TOKEN_TYPE,null)
    }

    fun getUUID(): String? {
        return prefs.getString(KEY_UUID, null)
    }

    companion object {
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
        const val KEY_TOKEN_TYPE = "key_token_type"
        const val KEY_UUID = "key_uuid"
    }
}