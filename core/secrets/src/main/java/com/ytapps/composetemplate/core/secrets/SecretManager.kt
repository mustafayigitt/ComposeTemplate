package com.ytapps.composetemplate.core.secrets

import android.content.Context
import com.ytapps.composetemplate.core.secrets.BuildConfig

/**
 * Manager for retrieving secrets from the NDK layer.
 */
object SecretManager {
    private var appContext: Context? = null

    init {
        if (BuildConfig.NATIVE_SECRETS_ENABLED) {
            System.loadLibrary("native-lib")
        }
    }

    /**
     * Initializes the SecretManager with the application context.
     * This must be called once at app startup.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private fun getSafeContext(): Context =
        checkNotNull(appContext) {
            "SecretManager must be initialized before use"
        }

    /**
     * Returns the API key based on the build type.
     */
    fun getApiKey(): String {
        return if (BuildConfig.NATIVE_SECRETS_ENABLED) {
            getApiKeyNative(getSafeContext(), BuildConfig.DEBUG, BuildConfig.K_PART)
        } else {
            if (BuildConfig.DEBUG) BuildConfig.API_KEY_DEBUG else BuildConfig.API_KEY_RELEASE
        }
    }

    /**
     * Returns the Base URL based on the build type.
     */
    fun getBaseUrl(): String {
        return if (BuildConfig.NATIVE_SECRETS_ENABLED) {
            getBaseUrlNative(getSafeContext(), BuildConfig.DEBUG, BuildConfig.K_PART)
        } else {
            if (BuildConfig.DEBUG) BuildConfig.BASE_URL_DEBUG else BuildConfig.BASE_URL_RELEASE
        }
    }

    private external fun getApiKeyNative(
        context: Context,
        isDebug: Boolean,
        runtimeMask: String,
    ): String

    private external fun getBaseUrlNative(
        context: Context,
        isDebug: Boolean,
        runtimeMask: String,
    ): String
}
