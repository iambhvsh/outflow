@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import `in`.iambhvsh.outflow.data.Category
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import `in`.iambhvsh.outflow.ui.components.AppButton
import `in`.iambhvsh.outflow.ui.components.AppInput
import `in`.iambhvsh.outflow.ui.components.AppSheet
import `in`.iambhvsh.outflow.ui.components.AppTextButton
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.color
import `in`.iambhvsh.outflow.ui.theme.container
import `in`.iambhvsh.outflow.ui.theme.flowColor
import `in`.iambhvsh.outflow.ui.theme.shape
import `in`.iambhvsh.outflow.ui.utils.atDate
import `in`.iambhvsh.outflow.ui.utils.currency
import `in`.iambhvsh.outflow.ui.utils.dateLabel
import `in`.iambhvsh.outflow.ui.utils.daysAgo
import `in`.iambhvsh.outflow.ui.utils.localToUtcDateMillis
import `in`.iambhvsh.outflow.ui.utils.shiftDay


@Composable
fun TransactionSheet(
    existing: TransactionEntity?,
    onSave: (TransactionEntity) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var type by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.type ?: TransactionType.OUTFLOW)
    }
    var amount by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.amount?.let { trimZeros(it) } ?: "")
    }
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var category by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.let { Category.of(it.category) } ?: Category.of(type).first())
    }
    var timestamp by rememberSaveable(existing?.id) {
        mutableLongStateOf(existing?.timestamp ?: System.currentTimeMillis())
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val value = amount.toDoubleOrNull() ?: 0.0

    AppSheet(
        title = if (existing == null) "New flow" else "Edit flow",
        onDismiss = onDismiss,
        modifier = modifier,
        trailing = {
            TypeMenu(type = type) { picked ->
                type = picked
                category = retype(category, picked)
            }
        }
    ) { dismiss ->
        val submit: () -> Unit = {
            if (value > 0.0) {
                onSave(
                    TransactionEntity(
                        id = existing?.id ?: 0L,
                        title = title.trim(),
                        amount = value,
                        category = category.name,
                        timestamp = timestamp,
                        type = type
                    )
                )
                dismiss()
            }
        }

        AppInput(
            value = amount,
            onValueChange = { amount = sanitise(it) },
            label = "Amount",
            prefix = currency.symbol,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        AppInput(
            value = title,
            onValueChange = { title = it },
            label = "What for?",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { submit() })
        )

        Text(
            text = "Date",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
        ) {
            val days = daysAgo(timestamp)
            val isToday = days == 0
            val isYesterday = days == 1
            val isCustom = !isToday && !isYesterday

            ToggleButton(
                checked = isToday,
                onCheckedChange = {
                    keyboard?.hide()
                    timestamp = shiftDay(timestamp, targetDaysAgo = 0)
                },
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Today", maxLines = 1)
            }

            ToggleButton(
                checked = isYesterday,
                onCheckedChange = {
                    keyboard?.hide()
                    timestamp = shiftDay(timestamp, targetDaysAgo = 1)
                },
                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Yesterday", maxLines = 1)
            }

            ToggleButton(
                checked = isCustom,
                onCheckedChange = {
                    keyboard?.hide()
                    showDatePicker = true
                },
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1.3f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isCustom) dateLabel(timestamp) else "Pick date",
                    modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        val categories = remember(type) { Category.of(type) }
        val categoryListState = rememberLazyListState()

        LaunchedEffect(category, type) {
            val idx = categories.indexOf(category)
            if (idx >= 0) {
                categoryListState.animateScrollToItem(idx)
            }
        }

        LazyRow(
            state = categoryListState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories, key = { it.name }) { option ->
                val selected = option == category
                ToggleButton(
                    checked = selected,
                    onCheckedChange = { category = option },
                    shapes = ToggleButtonDefaults.shapes(),
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        containerColor = OutflowColors.row,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedContainerColor = option.color,
                        checkedContentColor = if (OutflowColors.isDark) Color(0xFF141218) else Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(option.shape)
                            .background(if (selected) Color.White.copy(alpha = 0.22f) else option.container),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = if (selected) LocalContentColor.current else option.color,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = option.label,
                        modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                    )
                }
            }
        }

        AppButton(
            onClick = submit,
            modifier = Modifier.fillMaxWidth(),
            enabled = value > 0.0,
            contentPadding = PaddingValues(vertical = 18.dp)
        ) {
            Text(if (existing == null) "Add flow" else "Save changes")
        }
        AppTextButton(
            onClick = dismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = localToUtcDateMillis(timestamp)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                AppTextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { pickedUtc ->
                            timestamp = atDate(timestamp, pickedUtc)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                AppTextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


private val Types = listOf(TransactionType.OUTFLOW, TransactionType.INFLOW)

private val TransactionType.label: String
    get() = if (this == TransactionType.INFLOW) "Came in" else "Went out"

private val TransactionType.icon: ImageVector
    get() = if (this == TransactionType.INFLOW) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward


@Composable
private fun TypeMenu(
    type: TransactionType,
    modifier: Modifier = Modifier,
    onSelect: (TransactionType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val accent = flowColor(type == TransactionType.INFLOW)
    val turn by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "Chevron"
    )

    Box(modifier = modifier) {
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = {
                        val next = if (type == TransactionType.INFLOW) TransactionType.OUTFLOW else TransactionType.INFLOW
                        onSelect(next)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent.copy(alpha = 0.16f),
                        contentColor = accent
                    )
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = null,
                        modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = type.label,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    checked = expanded,
                    onCheckedChange = { expanded = it },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent.copy(alpha = 0.16f),
                        contentColor = accent
                    ),
                    modifier = Modifier.semantics {
                        stateDescription = if (expanded) "Expanded" else "Collapsed"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = "Change direction",
                        modifier = Modifier
                            .size(SplitButtonDefaults.TrailingIconSize)
                            .graphicsLayer { rotationZ = turn }
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.large
        ) {
            Types.forEachIndexed { index, option ->
                DropdownMenuItem(
                    selected = option == type,
                    onClick = {
                        expanded = false
                        if (option != type) onSelect(option)
                    },
                    text = { Text(option.label) },
                    shapes = MenuDefaults.itemShape(index, Types.size),
                    leadingIcon = {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = flowColor(option == TransactionType.INFLOW)
                        )
                    }
                )
            }
        }
    }
}


private fun retype(category: Category, type: TransactionType): Category =
    if (category.type == type) category else Category.of(type).first()


private fun sanitise(raw: String): String {
    val filtered = raw.filter { it.isDigit() || it == '.' }
    val point = filtered.indexOf('.')
    if (point < 0) return filtered
    return filtered.substring(0, point + 1) + filtered.substring(point + 1).filter { it.isDigit() }
}


private fun trimZeros(value: Double): String {
    val text = value.toString()
    if (!text.contains('.')) return text
    return text.trimEnd('0').trimEnd('.')
}
