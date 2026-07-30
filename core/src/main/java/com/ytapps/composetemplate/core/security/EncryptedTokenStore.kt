package com.ytapps.composetemplate.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keystore-backed token storage for runtime authentication credentials.
 *
 * A memory cache is kept for synchronous OkHttp interceptor/authenticator access,
 * while persisted values are encrypted at rest via EncryptedSharedPreferences.
 */
@Singleton
class EncryptedTokenStore @Inject constructor(
    @ApplicationContext context: Context
) : TokenStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _accessToken = MutableStateFlow(preferences.getString(KEY_ACCESS_TOKEN, null))
    private val _refreshToken = MutableStateFlow(preferences.getString(KEY_REFRESH_TOKEN, null))
    private val _tokenType = MutableStateFlow(preferences.getString(KEY_TOKEN_TYPE, null))

    override fun getAccessToken(): String? = _accessToken.value

    override fun getRefreshToken(): String? = _refreshToken.value

    override fun getTokenType(): String? = _tokenType.value

    override suspend fun setAccessToken(accessToken: String) {
        preferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
        _accessToken.value = accessToken
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        preferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        _refreshToken.value = refreshToken
    }

    override suspend fun setTokenType(tokenType: String) {
        preferences.edit().putString(KEY_TOKEN_TYPE, tokenType).apply()
        _tokenType.value = tokenType
    }

    override suspend fun clear() {
        preferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_TYPE)
            .apply()
        _accessToken.value = null
        _refreshToken.value = null
        _tokenType.value = null
    }

    override val accessTokenFlow: Flow<String?> = _accessToken.asStateFlow()

    override val refreshTokenFlow: Flow<String?> = _refreshToken.asStateFlow()

    override val tokenTypeFlow: Flow<String?> = _tokenType.asStateFlow()

    private companion object {
        const val PREFERENCES_NAME = "secure_auth_tokens"
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
        const val KEY_TOKEN_TYPE = "key_token_type"
    }
}
