package com.ytapps.composetemplate.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.common.Language
import com.ytapps.composetemplate.core.ui.BaseViewModel
import com.ytapps.composetemplate.feature.profile.domain.GetLanguageUseCase
import com.ytapps.composetemplate.feature.profile.domain.GetThemeUseCase
import com.ytapps.composetemplate.feature.profile.domain.LogoutUseCase
import com.ytapps.composetemplate.feature.profile.domain.UpdateLanguageUseCase
import com.ytapps.composetemplate.feature.profile.domain.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel
    @Inject
    constructor(
        private val updateTheme: UpdateThemeUseCase,
        private val getTheme: GetThemeUseCase,
        private val updateLanguage: UpdateLanguageUseCase,
        private val getLanguage: GetLanguageUseCase,
        private val logout: LogoutUseCase,
    ) : BaseViewModel<ProfileUiState, ProfileEvent>() {
        override val uiStateInternal = MutableStateFlow(ProfileUiState())

        init {
            observeTheme()
            observeLanguage()
        }

        private fun observeTheme() {
            viewModelScope.launch {
                getTheme.isDarkModeFlow.collectLatest { isDark ->
                    updateState { it.copy(isDarkMode = isDark) }
                }
            }
        }

        private fun observeLanguage() {
            viewModelScope.launch {
                getLanguage.invoke().collectLatest { language ->
                    updateState { it.copy(currentLanguage = language) }
                }
            }
        }

        fun onLanguageSelected(language: Language) {
            viewModelScope.launch {
                updateLanguage(language)
            }
        }

        fun toggleTheme() {
            viewModelScope.launch {
                updateTheme(!uiState.value.isDarkMode)
            }
        }

        fun logout() {
            viewModelScope.launch {
                logout.invoke()
                sendEvent(ProfileEvent.NavigateToLogin)
            }
        }
    }

sealed class ProfileEvent {
    data object NavigateToLogin : ProfileEvent()
}
