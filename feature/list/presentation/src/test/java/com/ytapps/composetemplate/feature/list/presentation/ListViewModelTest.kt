package com.ytapps.composetemplate.feature.list.presentation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class ListViewModelTest {
    private val viewModel = ListViewModel()

    @Test
    fun `given initial state when collect state then items empty`() {
        assertThat(viewModel.uiState.value.items).isEmpty()
    }

    @Test
    fun `given getItems called when collect state then items populated`() {
        viewModel.getItems()

        assertThat(viewModel.uiState.value.items).hasSize(100)
        assertThat(viewModel.uiState.value.items.first()).isEqualTo("Item 0")
        assertThat(viewModel.uiState.value.items.last()).isEqualTo("Item 99")
    }
}