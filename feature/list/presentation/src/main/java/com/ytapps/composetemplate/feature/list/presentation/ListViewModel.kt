package com.ytapps.composetemplate.feature.list.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class ListViewModel
    @Inject
    constructor() : BaseViewModel<ListUiState, Unit>() {
        override val uiStateInternal = MutableStateFlow(ListUiState())

        fun getItems() {
            updateState { state ->
                state.copy(
                    items =
                        listOf(
                            ListItemUiModel(
                                id = "architecture",
                                title = "Clean Architecture",
                                subtitle = "Feature modules split into data, domain, navigation, and presentation.",
                            ),
                            ListItemUiModel(
                                id = "networking",
                                title = "Networking",
                                subtitle = "Retrofit, OkHttp, token refresh, and repository error handling.",
                            ),
                            ListItemUiModel(
                                id = "security",
                                title = "Client Hardening",
                                subtitle = "Build-time secret validation, native access, and release checks.",
                            ),
                            ListItemUiModel(
                                id = "design-system",
                                title = "Design System",
                                subtitle = "Reusable Compose components, theme tokens, and preview helpers.",
                            ),
                        ),
                )
            }
        }
    }
