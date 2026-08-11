package com.ytapps.composetemplate.feature.detail.presentation

import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel
    @Inject
    constructor() : BaseViewModel<DetailUiState, Unit>() {
        override val uiStateInternal = MutableStateFlow(DetailUiState())

        fun setDetailId(id: String) {
            updateState {
                it.copy(
                    id = id,
                    title = id.toTitle(),
                    description = detailDescriptions[id].orEmpty(),
                )
            }
        }

        private fun String.toTitle(): String =
            split('-')
                .filter { it.isNotBlank() }
                .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }

        private companion object {
            val detailDescriptions =
                mapOf(
                    "architecture" to "Use feature modules to keep boundaries explicit and make replacement or removal cheap.",
                    "networking" to "Keep network concerns centralized so auth, logging, retries, and errors behave consistently.",
                    "security" to "Treat client hardening as layered friction, backed by build validation and release checks.",
                    "design-system" to "Build screens from shared primitives so product UI stays consistent as features grow.",
                )
        }
    }
