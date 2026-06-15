package com.ytapps.composetemplate.feature.profile.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
) : BaseViewModel<ProfileUiState, Unit>() {
    override val _uiState = MutableStateFlow(ProfileUiState)
}
