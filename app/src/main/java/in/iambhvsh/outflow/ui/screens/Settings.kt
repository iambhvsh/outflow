@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.BuildConfig
import `in`.iambhvsh.outflow.data.BackupData
import `in`.iambhvsh.outflow.data.exportBackup
import `in`.iambhvsh.outflow.data.importBackup
import `in`.iambhvsh.outflow.ui.AppModel
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppItem
import `in`.iambhvsh.outflow.ui.components.AppTextButton
import `in`.iambhvsh.outflow.ui.components.ColorPicker
import `in`.iambhvsh.outflow.ui.components.CurrencyPicker
import `in`.iambhvsh.outflow.ui.components.ThemePicker
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.utils.dateLabel
import `in`.iambhvsh.outflow.ui.utils.tally
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch


private val hasDynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S


@Composable
fun Settings(
    model: AppModel,
    padding: PaddingValues,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val options = model.options.collectAsStateWithLifecycle().value ?: return
    val count = model.count.collectAsStateWithLifecycle().value ?: 0
    var confirming by remember { mutableStateOf(false) }
    var pendingExport by remember { mutableStateOf<BackupData?>(null) }
    var pendingRestoreData by remember { mutableStateOf<BackupData?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            val data = pendingExport
            if (data != null) {
                val ok = exportBackup(context, uri, data)
                model.postMessage(if (ok) "Backup saved" else "Could not save backup")
            }
        }
        pendingExport = null
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val data = importBackup(context, uri)
            if (data != null) {
                pendingRestoreData = data
            } else {
                model.postMessage("Could not read backup file")
            }
        }
    }

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
        item(key = "export") {
            AppItem(
                title = "Back Up",
                index = 0,
                count = 3,
                icon = Icons.Rounded.FileUpload,
                description = if (count == 0) {
                    "Save your preferences"
                } else {
                    "Save a copy of your ${tally(count)} flows and preferences"
                },
                onClick = {
                    scope.launch {
                        val backup = model.createBackupData()
                        pendingExport = backup
                        val dateSuffix = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        exportLauncher.launch("outflow-backup-$dateSuffix.json")
                    }
                }
            )
        }
        item(key = "restore") {
            AppItem(
                title = "Restore",
                index = 1,
                count = 3,
                icon = Icons.Rounded.FileDownload,
                description = "Recover flows and preferences from a backup",
                onClick = {
                    importLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                }
            )
        }
        item(key = "clear") {
            AppItem(
                title = "Delete everything",
                index = 2,
                count = 3,
                icon = Icons.Rounded.DeleteOutline,
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

    val restoreData = pendingRestoreData
    if (restoreData != null) {
        val backupCount = restoreData.transactions.size
        AlertDialog(
            onDismissRequest = { pendingRestoreData = null },
            title = { Text("Restore Data?") },
            text = {
                Text(
                    "This backup includes ${tally(backupCount)} flows and preferences from ${dateLabel(restoreData.exportedAt)}.\n\n" +
                        "Choose 'Replace' to overwrite your current ledger, or 'Merge' to combine with existing flows."
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
            confirmButton = {
                AppTextButton(
                    onClick = {
                        model.restoreBackup(restoreData, replaceExisting = true) {
                            model.postMessage("Restored ${tally(backupCount)} flows and preferences")
                        }
                        pendingRestoreData = null
                    }
                ) {
                    Text("Replace", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                Row {
                    AppTextButton(
                        onClick = {
                            model.restoreBackup(restoreData, replaceExisting = false) {
                                model.postMessage("Merged ${tally(backupCount)} flows")
                            }
                            pendingRestoreData = null
                        }
                    ) {
                        Text("Merge")
                    }
                    AppTextButton(onClick = { pendingRestoreData = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
