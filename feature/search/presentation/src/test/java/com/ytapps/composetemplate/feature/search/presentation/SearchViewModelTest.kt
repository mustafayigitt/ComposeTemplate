package com.ytapps.composetemplate.feature.search.presentation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class SearchViewModelTest {
    private val viewModel = SearchViewModel()

    @Test
    fun `given initial state when collect state then default results shown`() {
        assertThat(viewModel.uiState.value.query).isEmpty()
        assertThat(viewModel.uiState.value.results).contains("Feature scaffolding")
    }

    @Test
    fun `given query changed when collect state then matching results shown`() {
        viewModel.onQueryChanged("secret")

        assertThat(viewModel.uiState.value.query).isEqualTo("secret")
        assertThat(viewModel.uiState.value.results).containsExactly("Secret validation")
    }
}
