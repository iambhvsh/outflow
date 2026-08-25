@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import `in`.iambhvsh.outflow.data.Currency
import `in`.iambhvsh.outflow.data.ThemeColor
import `in`.iambhvsh.outflow.data.ThemeMode
import `in`.iambhvsh.outflow.ui.theme.seed

private val ThemeMode.label: String
    get() = when (this) {
        ThemeMode.SYSTEM -> "Auto"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
        ThemeMode.OLED -> "Black"
    }

private val ThemeMode.icon: ImageVector
    get() = when (this) {
        ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
        ThemeMode.LIGHT -> Icons.Rounded.LightMode
        ThemeMode.DARK -> Icons.Rounded.DarkMode
        ThemeMode.OLED -> Icons.Rounded.Contrast
    }

/**
 * The four theme modes as one connected button group, icon-only since the current mode is named on the
 * line above. Both pickers are [AppCard]s rather than list items, which measure their slots with
 * intrinsics and cannot hold a scrolling child.
 */
@Composable
fun ThemePicker(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    AppCard(index = index, count = count, modifier = modifier, title = "Theme") {
        Text(
            text = selected.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
        ) {
            val modes = ThemeMode.entries
            modes.forEachIndexed { position, mode ->
                ToggleButton(
                    checked = mode == selected,
                    onCheckedChange = { if (mode != selected) onSelect(mode) },
                    modifier = Modifier.weight(1f),
                    shapes = when (position) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        modes.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    }
                ) {
                    Icon(mode.icon, contentDescription = mode.label)
                }
            }
        }
    }
}

/**
 * The four currencies as one connected button group, each button its own symbol — which is what every
 * figure in the app is prefixed with. The full name of the current pick sits on the line above.
 */
@Composable
fun CurrencyPicker(
    selected: Currency,
    onSelect: (Currency) -> Unit,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    AppCard(index = index, count = count, modifier = modifier, title = "Currency") {
        Text(
            text = selected.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
        ) {
            val currencies = Currency.entries
            currencies.forEachIndexed { position, option ->
                ToggleButton(
                    checked = option == selected,
                    onCheckedChange = { if (option != selected) onSelect(option) },
                    modifier = Modifier.weight(1f),
                    shapes = when (position) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        currencies.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    }
                ) {
                    Text(
                        text = option.symbol,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * The accent seeds as a scrolling row of swatches, dimmed and inert while dynamic colour is on. The
 * row bleeds to the card's trailing edge, so a cut-off swatch says there is more to drag into view.
 */
@Composable
fun ColorPicker(
    selected: ThemeColor,
    enabled: Boolean,
    onSelect: (ThemeColor) -> Unit,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    AppCard(index = index, count = count, modifier = modifier, title = "Accent", bleed = true) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeColor.entries.forEach { color ->
                FilledIconToggleButton(
                    checked = color == selected,
                    onCheckedChange = { if (color != selected) onSelect(color) },
                    enabled = enabled,
                    shapes = IconButtonDefaults.toggleableShapes(),
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        containerColor = color.seed,
                        contentColor = color.seed,
                        checkedContainerColor = color.seed,
                        checkedContentColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = color.seed.copy(alpha = 0.38f),
                        disabledContentColor = color.seed.copy(alpha = 0.38f)
                    )
                ) {
                    AnimatedVisibility(
                        visible = color == selected,
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = color.name,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
        }
    }
}
