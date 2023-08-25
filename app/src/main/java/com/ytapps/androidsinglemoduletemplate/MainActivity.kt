package com.ytapps.androidsinglemoduletemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ytapps.androidsinglemoduletemplate.ui.navigation.AppNavigation
import com.ytapps.androidsinglemoduletemplate.ui.theme.AndroidSingleModuleTemplateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSingleModuleTemplateTheme {
                AppNavigation()
            }
        }
    }
}