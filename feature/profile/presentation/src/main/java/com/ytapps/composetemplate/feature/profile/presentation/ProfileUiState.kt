package com.ytapps.composetemplate.feature.profile.presentation

import com.ytapps.composetemplate.core.common.Language

data class ProfileUiState(
    val isDarkMode: Boolean = false,
    val currentLanguage: Language = Language.ENGLISH,
    val isLoading: Boolean = false,
)
