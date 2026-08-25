package `in`.iambhvsh.outflow.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Scaffold insets with extra vertical padding. Horizontal insets are left to individual screens. */
fun screenPadding(padding: PaddingValues, top: Dp = 4.dp, bottom: Dp = 12.dp): PaddingValues =
    PaddingValues(
        top = padding.calculateTopPadding() + top,
        bottom = padding.calculateBottomPadding() + bottom
    )
