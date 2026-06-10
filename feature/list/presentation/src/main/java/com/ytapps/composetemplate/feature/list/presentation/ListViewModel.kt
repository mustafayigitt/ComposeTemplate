package com.ytapps.composetemplate.feature.list.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ListViewModel
    @Inject
    constructor() : BaseViewModel<ListUiState, Unit>(ListUiState()) {

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