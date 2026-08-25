@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import `in`.iambhvsh.outflow.ui.theme.HeroFont
import `in`.iambhvsh.outflow.ui.theme.OutflowColors

/**
 * The single top app bar every screen uses: one row, no subtitle, the title centred in the wide heavy
 * cut of Google Sans Flex, on the same surface the content sits on.
 *
 * One row scrolls away in full rather than collapsing, and its height is the only thing holding
 * content clear of the status bar — so a shell sharing one bar across destinations has to reset the
 * scroll state as it swaps them.
 */
@Composable
fun AppToolbar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = LocalTextStyle.current.copy(
                    fontFamily = HeroFont,
                    fontSize = 32.sp,
                    lineHeight = 32.sp
                )
            )
        },
        subtitle = {},
        modifier = modifier,
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = actions,
        titleHorizontalAlignment = Alignment.CenterHorizontally,
        colors = OutflowColors.topBar,
        scrollBehavior = scrollBehavior
    )
}
