package com.ytapps.composetemplate.core.theme.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.INavigationItem

@Composable
fun AppNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: INavigationItem,
    items: List<IBottomBarItem>,
    onItemClick: (IBottomBarItem) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = item.icon,
                selected = currentRoute.route == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { onItemClick(item) },
            )
        }
    }
}