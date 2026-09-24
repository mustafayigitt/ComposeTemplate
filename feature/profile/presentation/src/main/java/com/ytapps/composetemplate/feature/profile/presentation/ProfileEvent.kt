package com.ytapps.composetemplate.feature.profile.presentation

sealed class ProfileEvent {
    data object NavigateToHome : ProfileEvent()
}
