package com.ytapps.composetemplate.core.navigation

import com.google.common.truth.Truth

import org.junit.Before
import org.junit.Test

/**
 * Created by mustafa.yigit on 05/09/2023
 * mustafa.yt65@gmail.com
 */
class NavigationManagerTest {

    private lateinit var navigationManager: NavigationManager

    @Before
    fun setUp() {
        navigationManager = NavigationManager
    }

    @Test
    fun `given valid route when isBottomBarItem() then return true`() {
        // Given
        val route = com.ytapps.composetemplate.ui.home.Home.route

        // When
        val result = navigationManager.isBottomBarItem(route)

        // Then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `given invalid route when isBottomBarItem() then return false`() {
        // Given
        val route = "invalid_route"

        // When
        val result = navigationManager.isBottomBarItem(route)

        // Then
        Truth.assertThat(result).isFalse()
    }
}