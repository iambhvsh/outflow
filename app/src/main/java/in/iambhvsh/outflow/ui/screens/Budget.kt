@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.data.Category
import `in`.iambhvsh.outflow.data.TransactionType
import `in`.iambhvsh.outflow.ui.AppModel
import `in`.iambhvsh.outflow.ui.components.AppCard
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppInput
import `in`.iambhvsh.outflow.ui.components.AppItem
import `in`.iambhvsh.outflow.ui.components.AppLoader
import `in`.iambhvsh.outflow.ui.components.BudgetItem
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.theme.color
import `in`.iambhvsh.outflow.ui.theme.container
import `in`.iambhvsh.outflow.ui.utils.currency
import `in`.iambhvsh.outflow.ui.utils.money

@Composable
fun Budget(
    model: AppModel,
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val budgets = model.budgets.collectAsStateWithLifecycle().value

    if (budgets == null) {
        AppLoader(modifier = modifier.background(OutflowColors.group).padding(padding))
        return
    }

    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var isExisting by remember { mutableStateOf(false) }
    var limitInput by remember { mutableStateOf("") }

    val totalLimit = budgets.sumOf { it.limit }
    val totalSpent = budgets.sumOf { it.spent }
    val ratio = if (totalLimit > 0.0) (totalSpent / totalLimit).toFloat() else 0f

    val budgeted = budgets.map { it.category }.toSet()
    val available = Category.of(TransactionType.OUTFLOW).filter { it !in budgeted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "overview-heading") { AppHeading("Monthly overview") }
        item(key = "overview-card") {
            AppCard(index = 0, count = 1) {
                if (totalLimit == 0.0) {
                    Text(
                        text = "No budgets set",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Set monthly spending limits for categories below to keep your expenses in check.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = money(totalLimit),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Monthly target",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (totalSpent > totalLimit) {
                                    "${money(totalSpent - totalLimit)} over"
                                } else {
                                    "${money(totalLimit - totalSpent)} left"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (totalSpent > totalLimit) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                            Text(
                                text = "${money(totalSpent)} spent · ${(ratio * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    LinearWavyProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            ratio >= 1f -> MaterialTheme.colorScheme.error
                            ratio >= 0.85f -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        waveSpeed = 0.dp
                    )
                }
            }
        }

        if (budgets.isNotEmpty()) {
            item(key = "active-heading") { AppHeading("Active budgets (${budgets.size})") }
            itemsIndexed(
                items = budgets,
                key = { _, item -> item.category.name }
            ) { index, item ->
                BudgetItem(
                    budget = item,
                    index = index,
                    count = budgets.size,
                    onClick = {
                        editingCategory = item.category
                        isExisting = true
                        limitInput = trimZeros(item.limit)
                    }
                )
            }
        }

        if (available.isNotEmpty()) {
            item(key = "available-heading") {
                AppHeading(if (budgets.isEmpty()) "Categories" else "Available categories")
            }
            itemsIndexed(
                items = available,
                key = { _, cat -> cat.name }
            ) { index, cat ->
                AppItem(
                    title = cat.label,
                    index = index,
                    count = available.size,
                    icon = cat.icon,
                    description = "No budget limit set",
                    onClick = {
                        editingCategory = cat
                        isExisting = false
                        limitInput = ""
                    },
                    trailing = {
                        Text(
                            text = "+ Set",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }
    }

    if (editingCategory != null) {
        val cat = editingCategory!!
        val value = limitInput.toDoubleOrNull() ?: 0.0
        val submit = {
            if (value > 0.0) {
                model.saveBudget(cat, value)
                editingCategory = null
            }
        }

        AlertDialog(
            onDismissRequest = { editingCategory = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(cat.container),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = cat.icon,
                        contentDescription = cat.label,
                        tint = cat.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            title = {
                Text("${cat.label} budget")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Set your monthly spending limit for ${cat.label.lowercase()}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppInput(
                        value = limitInput,
                        onValueChange = { limitInput = sanitise(it) },
                        label = "Monthly limit",
                        prefix = currency.symbol,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submit() })
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(50, 100, 200, 500, 1000).forEach { preset ->
                            SuggestionChip(
                                onClick = { limitInput = preset.toString() },
                                label = { Text("${currency.symbol}$preset") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = submit,
                    enabled = value > 0.0
                ) {
                    Text(if (isExisting) "Update" else "Set")
                }
            },
            dismissButton = {
                Row {
                    if (isExisting) {
                        TextButton(
                            onClick = {
                                model.deleteBudget(cat)
                                editingCategory = null
                            }
                        ) {
                            Text(
                                text = "Remove",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    TextButton(onClick = { editingCategory = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

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
