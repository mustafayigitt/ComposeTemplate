package com.ytapps.composetemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ytapps.composetemplate.ui.AppNavigation
import com.ytapps.composetemplate.core.theme.ComposeTemplateTheme
import com.ytapps.composetemplate.core.navigation.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTemplateTheme {
                AppNavigation(navigationManager)
            }
        }
    }
}