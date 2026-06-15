package com.ytapps.composetemplate.feature.home.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
) : BaseViewModel<HomeUiState, Unit>() {
    override val _uiState = MutableStateFlow(HomeUiState)
}
