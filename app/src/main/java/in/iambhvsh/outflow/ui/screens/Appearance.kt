@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.ui.AppModel
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppItem
import `in`.iambhvsh.outflow.ui.components.ColorPicker
import `in`.iambhvsh.outflow.ui.components.ThemePicker
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes

private val hasDynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Dedicated page for customizing app theme, dynamic color, and accent color.
 */
@Composable
fun Appearance(
    model: AppModel,
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val options = model.options.collectAsStateWithLifecycle().value ?: return

    val colorCount = if (hasDynamicColor) 2 else 1

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "theme-heading") { AppHeading("Theme") }
        item(key = "theme") {
            ThemePicker(
                selected = options.themeMode,
                onSelect = { model.saveThemeMode(it) },
                index = 0,
                count = 1
            )
        }

        item(key = "color-heading") { AppHeading("Colour") }
        if (hasDynamicColor) {
            item(key = "dynamic") {
                AppItem(
                    title = "Dynamic colour",
                    index = 0,
                    count = colorCount,
                    description = "Take the accent from your wallpaper",
                    onClick = { model.saveIsDynamicColor(!options.isDynamicColor) },
                    trailing = {
                        Switch(
                            checked = options.isDynamicColor,
                            onCheckedChange = { model.saveIsDynamicColor(it) },
                            colors = OutflowColors.switch
                        )
                    }
                )
            }
        }
        item(key = "accent") {
            ColorPicker(
                selected = options.themeColor,
                enabled = !options.isDynamicColor,
                onSelect = { model.saveThemeColor(it) },
                index = colorCount - 1,
                count = colorCount
            )
        }
    }
}
