package com.lhacenmed.budget.util

data class DarkThemePreference(
    val darkThemeValue: Int = FOLLOW_SYSTEM,
    val isHighContrastModeEnabled: Boolean = false,
) {
    companion object {
        const val FOLLOW_SYSTEM = 0
        const val OFF = 1
        const val ON = 2
    }

    fun isDarkTheme(isSystemInDarkTheme: Boolean = false): Boolean = when (darkThemeValue) {
        ON -> true
        OFF -> false
        else -> isSystemInDarkTheme
    }

    fun isFollowSystem() = darkThemeValue == FOLLOW_SYSTEM

    fun getDarkThemeDesc(): String = when (darkThemeValue) {
        ON -> "On"
        OFF -> "Off"
        else -> "Follow system"
    }
}
