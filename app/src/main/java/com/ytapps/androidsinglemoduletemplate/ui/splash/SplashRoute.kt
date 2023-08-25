package com.ytapps.androidsinglemoduletemplate.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ytapps.androidsinglemoduletemplate.ui.home.Home
import com.ytapps.androidsinglemoduletemplate.ui.navigation.INavigationItem

/**
 * Created by mustafayigitt on 25/08/2023
 * mustafa.yt65@gmail.com
 */

fun NavGraphBuilder.splashGraph(
    navController: NavController
) {
    composable(Splash.route) {
        SplashScreen(
            homeNavigator = {
                navController.navigate(Home.route)
            }
        )
    }
}

data object Splash : INavigationItem {
    override val route: String = "route_splash"
}

@Composable
fun SplashScreen(
    homeNavigator: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Splash Screen")
        Button(
            modifier = Modifier.offset(y = 48.dp),
            onClick = {
                homeNavigator.invoke()
            },
        ) {
            Text(text = "Go to Home")
        }
    }
}