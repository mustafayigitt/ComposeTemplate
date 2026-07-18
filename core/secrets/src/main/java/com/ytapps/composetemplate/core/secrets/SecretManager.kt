package com.ytapps.composetemplate.core.secrets

import android.content.Context
import com.ytapps.composetemplate.core.secrets.BuildConfig

/**
 * Manager for retrieving secrets from the NDK layer.
 */
object SecretManager {
    private var appContext: Context? = null

    init {
        System.loadLibrary("native-lib")
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
    fun getApiKey(): String = getApiKeyNative(getSafeContext(), BuildConfig.DEBUG)

    /**
     * Returns the Base URL based on the build type.
     */
    fun getBaseUrl(): String = getBaseUrlNative(getSafeContext(), BuildConfig.DEBUG)

    private external fun getApiKeyNative(
        context: Context,
        isDebug: Boolean,
    ): String

    private external fun getBaseUrlNative(
        context: Context,
        isDebug: Boolean,
    ): String
}
