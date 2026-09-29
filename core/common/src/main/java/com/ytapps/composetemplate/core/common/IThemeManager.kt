package com.ytapps.composetemplate.core.common

import kotlinx.coroutines.flow.StateFlow

/** App-level theme state independent from navigation. */
interface IThemeManager {
    val isDarkModeFlow: StateFlow<Boolean>
}
