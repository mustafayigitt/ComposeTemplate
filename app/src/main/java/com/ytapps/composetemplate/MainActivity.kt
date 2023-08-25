package com.ytapps.composetemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ytapps.composetemplate.ui.navigation.AppNavigation
import com.ytapps.composetemplate.ui.theme.ComposeTemplateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTemplateTheme {
                AppNavigation()
            }
        }
    }
}