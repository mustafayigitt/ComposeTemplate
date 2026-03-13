package com.lhacenmed.budget.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator()
    LoadingIndicator()
    ContainedLoadingIndicator()
}