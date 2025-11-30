package com.ytapps.composetemplate.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 25/08/2023
 * mustafa.yt65@gmail.com
 */
@HiltViewModel
class ListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState = _uiState.asStateFlow()

    fun getItems() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    items = List(100) {
                        "Item $it"
                    }
                )
            }
        }
    }
}