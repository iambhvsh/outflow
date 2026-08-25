@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.BuildConfig
import `in`.iambhvsh.outflow.ui.AppModel
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppItem
import `in`.iambhvsh.outflow.ui.components.AppTextButton
import `in`.iambhvsh.outflow.ui.components.ColorPicker
import `in`.iambhvsh.outflow.ui.components.CurrencyPicker
import `in`.iambhvsh.outflow.ui.components.ThemePicker
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.utils.tally


private val hasDynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S


@Composable
fun Settings(
    model: AppModel,
    padding: PaddingValues,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = model.options.collectAsStateWithLifecycle().value ?: return
    val count = model.count.collectAsStateWithLifecycle().value ?: 0
    var confirming by remember { mutableStateOf(false) }

    val appearance = if (hasDynamicColor) 3 else 2

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "appearance-heading") { AppHeading("Appearance") }
        item(key = "theme") {
            ThemePicker(
                selected = options.themeMode,
                onSelect = { model.saveThemeMode(it) },
                index = 0,
                count = appearance
            )
        }
        if (hasDynamicColor) {
            item(key = "dynamic") {
                AppItem(
                    title = "Dynamic colour",
                    index = 1,
                    count = appearance,
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
                index = appearance - 1,
                count = appearance
            )
        }

        item(key = "money-heading") { AppHeading("Money") }
        item(key = "currency") {
            CurrencyPicker(
                selected = options.currency,
                onSelect = { model.saveCurrency(it) },
                index = 0,
                count = 1
            )
        }

        item(key = "data-heading") { AppHeading("Data") }
        item(key = "clear") {
            AppItem(
                title = "Delete everything",
                index = 0,
                count = 1,
                description = if (count == 0) {
                    "Nothing recorded yet"
                } else {
                    "Removes all ${tally(count)} flows for good"
                },
                onClick = { if (count > 0) confirming = true }
            )
        }

        item(key = "about-heading") { AppHeading("About") }
        item(key = "about") {
            AppItem(
                title = "Outflow",
                index = 0,
                count = 1,
                description = "Version ${BuildConfig.VERSION_NAME}",
                onClick = onAbout
            )
        }
    }

    if (confirming) {
        AlertDialog(
            onDismissRequest = { confirming = false },
            title = { Text("Delete everything?") },
            text = { Text("All ${tally(count)} flows will be removed. This cannot be undone.") },
            shape = MaterialTheme.shapes.extraLarge,
            confirmButton = {
                AppTextButton(
                    onClick = {
                        model.clear()
                        confirming = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                AppTextButton(onClick = { confirming = false }) {
                    Text("Keep")
                }
            }
        )
    }
}
