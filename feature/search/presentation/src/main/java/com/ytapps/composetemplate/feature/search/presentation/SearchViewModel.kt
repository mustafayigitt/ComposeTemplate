package com.ytapps.composetemplate.feature.search.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel
    @Inject
    constructor() : BaseViewModel<SearchUiState, Unit>() {
        override val uiStateInternal = MutableStateFlow(SearchUiState)
    }
