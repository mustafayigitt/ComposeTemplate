package com.ytapps.composetemplate.feature.profile.presentation

sealed class ProfileEvent {
    data object NavigateToLogin : ProfileEvent()
}
