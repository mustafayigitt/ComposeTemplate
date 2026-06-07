package com.ytapps.composetemplate.core.util

import android.content.Context
import com.ytapps.composetemplate.core.BuildConfig

/**
 * Manager for retrieving secrets from the NDK layer.
 */
object SecretManager {
    init {
        System.loadLibrary("native-lib")
    }

    /**
     * Returns the API key based on the build type.
     */
    fun getApiKey(context: Context): String = getApiKeyNative(context, BuildConfig.DEBUG)

    private external fun getApiKeyNative(
        context: Context,
        isDebug: Boolean,
    ): String
}
