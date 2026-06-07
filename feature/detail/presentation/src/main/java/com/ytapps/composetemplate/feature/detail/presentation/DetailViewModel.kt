package com.ytapps.composetemplate.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiState = MutableStateFlow(DetailUiState())
        val uiState = _uiState.asStateFlow()

        fun setDetailId(id: String) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(id = id)
                }
            }
        }
    }
