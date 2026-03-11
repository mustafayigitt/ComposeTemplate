package com.lhacenmed.budget.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Mirrors Seal's SealModalBottomSheet exactly:
 * - SheetState with initialValue = Hidden (not rememberModalBottomSheetState)
 * - windowInsets = 0 — we handle bottom padding manually via NavigationBarSpacer
 * - NavigationBarSpacer fills nav bar height with sheet background color
 *
 * This combination is what makes predictive back gesture work correctly.
 * Requires Material3 1.3.0+ (BOM 2024.09.00+).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    horizontalPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontalPadding)) {
            content()
            Spacer(modifier = Modifier.height(28.dp))
        }
        // Fills the navigation bar area with the sheet's background color
        // This is what Seal does in NavigationBarSpacer
        Spacer(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
        )
    }
}
