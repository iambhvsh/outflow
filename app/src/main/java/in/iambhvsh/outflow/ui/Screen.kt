package `in`.iambhvsh.outflow.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The three top-level destinations, in the order the toolbar shows them. [icon] is drawn while
 * unselected and [active] once selected.
 */
enum class Screen(
    val label: String,
    val icon: ImageVector,
    val active: ImageVector
) {
    HOME("Flows", Icons.Outlined.Wallet, Icons.Rounded.Wallet),
    INSIGHT("Insights", Icons.Outlined.Insights, Icons.Rounded.Insights),
    SETTINGS("Settings", Icons.Outlined.Settings, Icons.Rounded.Settings)
}
