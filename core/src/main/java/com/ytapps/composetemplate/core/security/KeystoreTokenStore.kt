package com.ytapps.composetemplate.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_tokens"
)

/**
 * TokenStore implementation backed by DataStore and Android Keystore.
 *
 * Token values are encrypted with AES/GCM before being written to DataStore. This
 * avoids the deprecated EncryptedSharedPreferences/security-crypto path while
 * keeping synchronous in-memory reads available for OkHttp interceptors.
 */
@Singleton
internal class KeystoreTokenStore @Inject constructor(
    @ApplicationContext context: Context
) : TokenStore {

    private val dataStore = context.tokenDataStore
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cryptoLock = Any()

    private val cachedTokens: StateFlow<AuthTokens?> = dataStore.data
        .map { preferences -> preferences.toAuthTokensOrNull() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override fun getTokens(): AuthTokens? = cachedTokens.value

    override suspend fun saveTokens(tokens: AuthTokens) {
        val encryptedAccessToken = encrypt(tokens.accessToken)
        val encryptedRefreshToken = encrypt(tokens.refreshToken)

        dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = encryptedAccessToken
            preferences[Keys.REFRESH_TOKEN] = encryptedRefreshToken
            preferences[Keys.TOKEN_TYPE] = tokens.tokenType
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(Keys.ACCESS_TOKEN)
            preferences.remove(Keys.REFRESH_TOKEN)
            preferences.remove(Keys.TOKEN_TYPE)
        }
    }

    override val tokensFlow: Flow<AuthTokens?> = cachedTokens

    private suspend fun Preferences.toAuthTokensOrNull(): AuthTokens? = withContext(Dispatchers.IO) {
        val encryptedAccessToken = this@toAuthTokensOrNull[Keys.ACCESS_TOKEN]
        val encryptedRefreshToken = this@toAuthTokensOrNull[Keys.REFRESH_TOKEN]

        if (encryptedAccessToken.isNullOrBlank() || encryptedRefreshToken.isNullOrBlank()) {
            return@withContext null
        }

        runCatching {
            AuthTokens(
                accessToken = decrypt(encryptedAccessToken),
                refreshToken = decrypt(encryptedRefreshToken),
                tokenType = this@toAuthTokensOrNull[Keys.TOKEN_TYPE] ?: AuthTokens.DEFAULT_TOKEN_TYPE
            )
        }.getOrNull()
    }

    private suspend fun encrypt(value: String): String = withContext(Dispatchers.IO) {
        synchronized(cryptoLock) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv

            "${iv.base64()}$ENCRYPTED_VALUE_SEPARATOR${encrypted.base64()}"
        }
    }

    private fun decrypt(value: String): String {
        return synchronized(cryptoLock) {
            val parts = value.split(ENCRYPTED_VALUE_SEPARATOR)
            require(parts.size == ENCRYPTED_VALUE_PARTS)

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE_BITS)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    private fun ByteArray.base64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        val TOKEN_TYPE = stringPreferencesKey("key_token_type")
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "com.ytapps.composetemplate.auth_tokens"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH_BITS = 128
        const val KEY_SIZE_BITS = 256
        const val ENCRYPTED_VALUE_SEPARATOR = ":"
        const val ENCRYPTED_VALUE_PARTS = 2
    }
}
