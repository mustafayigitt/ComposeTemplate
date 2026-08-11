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

        assertThat(viewModel.uiState.value.items).hasSize(4)
        assertThat(
            viewModel.uiState.value.items
                .first(),
        ).isEqualTo(
            ListItemUiModel(
                id = "architecture",
                title = "Clean Architecture",
                subtitle = "Feature modules split into data, domain, navigation, and presentation.",
            ),
        )
        assertThat(
            viewModel.uiState.value.items
                .last(),
        ).isEqualTo(
            ListItemUiModel(
                id = "design-system",
                title = "Design System",
                subtitle = "Reusable Compose components, theme tokens, and preview helpers.",
            ),
        )
    }
}
