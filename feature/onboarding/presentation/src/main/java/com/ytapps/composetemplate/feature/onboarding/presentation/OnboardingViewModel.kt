package com.ytapps.composetemplate.feature.onboarding.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.ui.BaseViewModel
import com.ytapps.composetemplate.feature.onboarding.domain.CompleteOnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel
    @Inject
    constructor(
        private val completeOnboarding: CompleteOnboardingUseCase,
    ) : BaseViewModel<OnboardingUiState, OnboardingEvent>() {
        override val uiStateInternal = MutableStateFlow(OnboardingUiState())

        fun onPageChanged(page: Int) {
            updateState { it.copy(currentPage = page) }
        }

        fun complete() {
            viewModelScope.launch {
                completeOnboarding()
                sendEvent(OnboardingEvent.NavigateToLogin)
            }
        }
    }

sealed class OnboardingEvent {
    object NavigateToLogin : OnboardingEvent()
}
