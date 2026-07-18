package com.ytapps.composetemplate.core.googleplay

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.ytapps.composetemplate.core.config.ConfigKey
import com.ytapps.composetemplate.core.config.IConfigManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val configManager: IConfigManager,
    ) {
        private val appUpdateManager = AppUpdateManagerFactory.create(context)

        /**
         * Checks for Google Play updates.
         */
        fun checkForPlayUpdate(activity: Activity) {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                        REQUEST_CODE_UPDATE,
                    )
                }
            }
        }

        /**
         * Custom force update logic using Remote Config.
         */
        fun isForceUpdateRequired(currentVersionCode: Long): Boolean {
            val minVersionCode = configManager.getLong(ConfigKey.MIN_VERSION)
            return currentVersionCode < minVersionCode
        }

        companion object {
            const val REQUEST_CODE_UPDATE = 1001
        }
    }
