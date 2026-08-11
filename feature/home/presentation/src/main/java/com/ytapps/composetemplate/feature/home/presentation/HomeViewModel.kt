package com.ytapps.composetemplate.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor() : BaseViewModel<HomeUiState, Nothing>() {
        override val uiStateInternal = MutableStateFlow(HomeUiState())

        init {
            viewModelScope.launch {
                updateState { it.copy(isLoading = false) }
            }
        }
    }
