package com.ytapps.composetemplate.feature.list.presentation

internal data class ListUiState(
    val items: List<ListItemUiModel> = emptyList(),
)

internal data class ListItemUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
)
