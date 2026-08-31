package com.ytapps.composetemplate

import android.app.Application
import com.ytapps.composetemplate.core.common.initializer.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    /**
     * Startup work contributed by optional modules. The set is empty when every
     * contributing module has been removed, so nothing here needs to change when a module
     * is plugged out.
     */
    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initializers
            .sortedBy { it.order }
            .forEach { it.init(this) }
    }
}
