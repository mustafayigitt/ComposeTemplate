package com.ytapps.composetemplate.feature.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ListViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiState = MutableStateFlow(ListUiState())
        val uiState = _uiState.asStateFlow()

        fun getItems() {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(
                        items =
                            List(ITEM_COUNT) {
                                "Item $it"
                            },
                    )
                }
            }
        }

        private companion object {
            const val ITEM_COUNT = 100
        }
    }
