@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.iambhvsh.outflow.data.BudgetProgress
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.theme.color
import `in`.iambhvsh.outflow.ui.theme.container
import `in`.iambhvsh.outflow.ui.theme.shape
import `in`.iambhvsh.outflow.ui.utils.money

@Composable
fun BudgetItem(
    budget: BudgetProgress,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = budget.category
    val shape = OutflowShapes.item(index, count)

    val barColor = when {
        budget.isExceeded -> MaterialTheme.colorScheme.error
        budget.isNearLimit -> MaterialTheme.colorScheme.tertiary
        else -> category.color
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(OutflowColors.row)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(category.shape)
                    .background(category.container),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.label,
                    tint = category.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${money(budget.spent)} of ${money(budget.limit)} · ${(budget.ratio * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (budget.isExceeded) {
                    "${money(budget.spent - budget.limit)} over"
                } else {
                    "${money(budget.remaining)} left"
                },
                style = MaterialTheme.typography.labelLarge,
                color = if (budget.isExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LinearWavyProgressIndicator(
            progress = { budget.ratio.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            waveSpeed = 0.dp
        )
    }
}
