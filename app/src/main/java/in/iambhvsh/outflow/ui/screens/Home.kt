@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.ui.AppModel
import `in`.iambhvsh.outflow.ui.Summary
import `in`.iambhvsh.outflow.ui.components.AppCard
import `in`.iambhvsh.outflow.ui.components.AppEmpty
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppLoader
import `in`.iambhvsh.outflow.ui.components.AppTile
import `in`.iambhvsh.outflow.ui.components.Segment
import `in`.iambhvsh.outflow.ui.components.StackedBar
import `in`.iambhvsh.outflow.ui.components.TileRow
import `in`.iambhvsh.outflow.ui.components.TransactionItem
import `in`.iambhvsh.outflow.ui.theme.HeroFont
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.utils.compact
import `in`.iambhvsh.outflow.ui.utils.dayLabel
import `in`.iambhvsh.outflow.ui.utils.money
import `in`.iambhvsh.outflow.ui.utils.signed
import kotlinx.coroutines.flow.distinctUntilChanged

/** How many rows from the end of the loaded list the next page is fetched at. */
private const val Reach = 24

/**
 * The balance, the month at a glance, then every flow grouped by the day it happened on. The list
 * holds a page of the ledger and fetches the next well before the end of the current one is in view.
 */
@Composable
fun Home(
    model: AppModel,
    padding: PaddingValues,
    onEdit: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = model.days.collectAsStateWithLifecycle().value
    val count = model.count.collectAsStateWithLifecycle().value
    val lifetime = model.lifetime.collectAsStateWithLifecycle().value
    val month = model.month.collectAsStateWithLifecycle().value
    val list = rememberLazyListState()

    LaunchedEffect(list) {
        snapshotFlow {
            val layout = list.layoutInfo
            (layout.visibleItemsInfo.lastOrNull()?.index ?: 0) to layout.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (last, total) -> if (total > 0 && last >= total - Reach) model.expand() }
    }


    if (days == null || count == null || lifetime == null || month == null) {
        AppLoader(modifier = modifier.background(OutflowColors.group).padding(padding))
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        state = list,
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "balance") { Balance(lifetime) }

        item(key = "month-heading") { AppHeading("This month") }
        item(key = "month-tiles") {
            TileRow {
                AppTile(
                    value = compact(month.inflow),
                    label = "Came in",
                    container = MaterialTheme.colorScheme.primaryContainer,
                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                    icon = Icons.Rounded.ArrowDownward
                )
                AppTile(
                    value = compact(month.outflow),
                    label = "Went out",
                    container = MaterialTheme.colorScheme.tertiaryContainer,
                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                    icon = Icons.Rounded.ArrowUpward
                )
            }
        }


        if (count == 0) {
            item(key = "empty") {
                AppEmpty(
                    title = "No flows yet",
                    description = "Add your first inflow or outflow and it will show up here, grouped by day.",
                    icon = Icons.Rounded.Savings
                )
            }
            return@LazyColumn
        }

        days.forEach { day ->
            item(key = "day-${day.timestamp}") {
                AppHeading(
                    text = dayLabel(day.timestamp),
                    trailing = {
                        Text(
                            text = signed(day.summary.net, day.summary.net >= 0),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
            itemsIndexed(
                items = day.transactions,
                key = { _, transaction -> transaction.id }
            ) { index, transaction ->
                TransactionItem(
                    transaction = transaction,
                    index = index,
                    count = day.transactions.size,
                    onClick = { onEdit(transaction) },
                    onDelete = { model.delete(transaction) }
                )
            }
        }
    }
}

/** The lifetime net, with the split that produced it. */
@Composable
private fun Balance(summary: Summary) {
    AppCard(index = 0, count = 1) {
        Text(
            text = "Net balance",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = money(summary.net),
            modifier = Modifier.fillMaxWidth(),
            fontFamily = HeroFont,
            autoSize = TextAutoSize.StepBased(minFontSize = 28.sp, maxFontSize = 52.sp),
            maxLines = 1
        )
        StackedBar(
            segments = listOf(
                Segment("In", summary.inflow, OutflowColors.inflow),
                Segment("Out", summary.outflow, OutflowColors.outflow)
            ),
            height = 12.dp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${money(summary.inflow)} in",
                style = MaterialTheme.typography.bodyMedium,
                color = OutflowColors.inflow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${money(summary.outflow)} out",
                style = MaterialTheme.typography.bodyMedium,
                color = OutflowColors.outflow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
