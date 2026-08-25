@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.ui.AppModel
import `in`.iambhvsh.outflow.ui.Slice
import `in`.iambhvsh.outflow.ui.components.AppCard
import `in`.iambhvsh.outflow.ui.components.AppEmpty
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppItem
import `in`.iambhvsh.outflow.ui.components.AppLoader
import `in`.iambhvsh.outflow.ui.components.AppTile
import `in`.iambhvsh.outflow.ui.components.BarLegend
import `in`.iambhvsh.outflow.ui.components.DayChart
import `in`.iambhvsh.outflow.ui.components.Segment
import `in`.iambhvsh.outflow.ui.components.StackedBar
import `in`.iambhvsh.outflow.ui.components.TileRow
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.theme.color
import `in`.iambhvsh.outflow.ui.utils.compact
import `in`.iambhvsh.outflow.ui.utils.money
import `in`.iambhvsh.outflow.ui.utils.signed
import `in`.iambhvsh.outflow.ui.utils.tally


private const val TopCategories = 5


private val FigureBaseline = 5.2.dp

/** Today, the week, where this month's money went, and the totals behind all of it. */
@Composable
fun Insight(
    model: AppModel,
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val today = model.today.collectAsStateWithLifecycle().value
    val week = model.week.collectAsStateWithLifecycle().value
    val slices = model.slices.collectAsStateWithLifecycle().value
    val lifetime = model.lifetime.collectAsStateWithLifecycle().value
    val count = model.count.collectAsStateWithLifecycle().value


    if (today == null || week == null || slices == null || lifetime == null || count == null) {
        AppLoader(modifier = modifier.background(OutflowColors.group).padding(padding))
        return
    }

    if (count == 0) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(OutflowColors.group)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            AppEmpty(
                title = "Nothing to read yet",
                description = "Insights show up once you have a few flows to compare — charts, category splits and totals.",
                icon = Icons.Rounded.Insights
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "today-heading") { AppHeading("Today") }
        item(key = "today-tiles") {
            TileRow {
                AppTile(
                    value = compact(today.inflow),
                    label = "Came in",
                    container = MaterialTheme.colorScheme.primaryContainer,
                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                    icon = Icons.Rounded.ArrowDownward
                )
                AppTile(
                    value = compact(today.outflow),
                    label = "Went out",
                    container = MaterialTheme.colorScheme.tertiaryContainer,
                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                    icon = Icons.Rounded.ArrowUpward
                )
            }
        }

        item(key = "week-heading") { AppHeading("This week") }
        item(key = "week-chart") {
            AppCard(index = 0, count = 1, bleed = true) {
                Row(
                    modifier = Modifier.padding(end = 20.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = money(week.sumOf { it.summary.outflow }),
                        style = MaterialTheme.typography.displaySmall,
                        maxLines = 1
                    )
                    Text(
                        text = "spent",
                        modifier = Modifier.padding(start = 6.dp, bottom = FigureBaseline),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DayChart(days = week)
            }
        }

        item(key = "categories-heading") { AppHeading("Where it went") }
        item(key = "categories") {
            val segments = bands(slices)
            AppCard(index = 0, count = 1) {
                if (segments.isEmpty()) {
                    Text(
                        text = "You have not spent anything this month.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    StackedBar(segments = segments)
                    BarLegend(segments = segments)
                }
            }
        }

        item(key = "lifetime-heading") { AppHeading("All time") }
        item(key = "lifetime-in") {
            AppItem(
                title = "Came in",
                index = 0,
                count = 4,
                trailing = {
                    Text(
                        text = money(lifetime.inflow),
                        style = MaterialTheme.typography.titleMedium,
                        color = OutflowColors.inflow
                    )
                }
            )
        }
        item(key = "lifetime-out") {
            AppItem(
                title = "Went out",
                index = 1,
                count = 4,
                trailing = {
                    Text(
                        text = money(lifetime.outflow),
                        style = MaterialTheme.typography.titleMedium,
                        color = OutflowColors.outflow
                    )
                }
            )
        }
        item(key = "lifetime-net") {
            AppItem(
                title = "Net",
                index = 2,
                count = 4,
                trailing = {
                    Text(
                        text = signed(lifetime.net, lifetime.net >= 0),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
        item(key = "lifetime-count") {
            AppItem(
                title = "Flows recorded",
                index = 3,
                count = 4,
                trailing = {
                    Text(
                        text = tally(count),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}


@Composable
private fun bands(slices: List<Slice>): List<Segment> {
    if (slices.isEmpty()) return emptyList()
    val head = slices.take(TopCategories).map { Segment(it.category.label, it.amount, it.category.color) }
    val tail = slices.drop(TopCategories)
    if (tail.isEmpty()) return head
    return head + Segment("Rest", tail.sumOf { it.amount }, MaterialTheme.colorScheme.outline)
}
