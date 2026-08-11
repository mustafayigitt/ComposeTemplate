package com.ytapps.composetemplate.feature.search.presentation

internal data class SearchUiState(
    val query: String = "",
    val results: List<String> = defaultResults,
) {
    companion object {
        val defaultResults =
            listOf(
                "Feature scaffolding",
                "Secret validation",
                "Token refresh",
                "Offline-first database",
                "Baseline profiles",
                "Design system",
            )
    }
}
