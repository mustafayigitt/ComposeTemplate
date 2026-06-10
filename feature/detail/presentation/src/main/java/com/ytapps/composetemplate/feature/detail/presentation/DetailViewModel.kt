package com.ytapps.composetemplate.feature.detail.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel
    @Inject
    constructor() : BaseViewModel<DetailUiState, Unit>(DetailUiState()) {

        fun setDetailId(id: String) {
            updateState {
                it.copy(id = id)
            }
        }
    }