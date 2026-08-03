package com.ytapps.composetemplate.feature.detail.presentation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class DetailViewModelTest {
    private val viewModel = DetailViewModel()

    @Test
    fun `given initial state when collect state then id empty`() {
        assertThat(viewModel.uiState.value.id).isEmpty()
    }

    @Test
    fun `given setDetailId called when collect state then id updated`() {
        viewModel.setDetailId("design-system")

        assertThat(viewModel.uiState.value.id).isEqualTo("design-system")
        assertThat(viewModel.uiState.value.title).isEqualTo("Design System")
        assertThat(viewModel.uiState.value.description).isNotEmpty()
    }
}
