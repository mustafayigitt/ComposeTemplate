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
        viewModel.setDetailId("test-id-42")

        assertThat(viewModel.uiState.value.id).isEqualTo("test-id-42")
    }
}