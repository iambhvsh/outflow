@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import `in`.iambhvsh.outflow.data.Category
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import `in`.iambhvsh.outflow.ui.components.AppButton
import `in`.iambhvsh.outflow.ui.components.AppInput
import `in`.iambhvsh.outflow.ui.components.AppSheet
import `in`.iambhvsh.outflow.ui.components.AppTextButton
import `in`.iambhvsh.outflow.ui.theme.color
import `in`.iambhvsh.outflow.ui.theme.flowColor
import `in`.iambhvsh.outflow.ui.utils.currency


@Composable
fun TransactionSheet(
    existing: TransactionEntity?,
    onSave: (TransactionEntity) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var type by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.type ?: TransactionType.OUTFLOW)
    }
    var amount by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.amount?.let { trimZeros(it) } ?: "")
    }
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var category by rememberSaveable(existing?.id) {
        mutableStateOf(Category.of(existing?.category))
    }

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
                        timestamp = existing?.timestamp ?: System.currentTimeMillis(),
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
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Category.of(type), key = { it.name }) { option ->
                ToggleButton(
                    checked = option == category,
                    onCheckedChange = { category = option },
                    shapes = ToggleButtonDefaults.shapes(),
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        checkedContainerColor = option.color,
                        checkedContentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Icon(option.icon, contentDescription = null)
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
        ToggleButton(
            checked = expanded,
            onCheckedChange = { expanded = it },
            shapes = ToggleButtonDefaults.shapes(),
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = accent.copy(alpha = 0.16f),
                contentColor = accent,
                checkedContainerColor = accent,
                checkedContentColor = MaterialTheme.colorScheme.surface
            ),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp)
        ) {
            Text(
                text = type.label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = "Change direction",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(20.dp)
                    .rotate(turn)
            )
        }

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
