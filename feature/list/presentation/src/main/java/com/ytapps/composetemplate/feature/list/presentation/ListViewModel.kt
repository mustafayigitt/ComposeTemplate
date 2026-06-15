package com.ytapps.composetemplate.feature.list.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class ListViewModel @Inject constructor(
) : BaseViewModel<ListUiState, Unit>() {
    override val _uiState = MutableStateFlow(ListUiState())

    fun getItems() {
        updateState { state ->
            state.copy(
                items =
                    List(ITEM_COUNT) {
                        "Item $it"
                    },
            )
        }
    }

    private companion object {
        const val ITEM_COUNT = 100
    }
}
