@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.iambhvsh.outflow.data.Category
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.theme.color
import `in`.iambhvsh.outflow.ui.theme.container
import `in`.iambhvsh.outflow.ui.theme.flowColor
import `in`.iambhvsh.outflow.ui.utils.signed
import `in`.iambhvsh.outflow.ui.utils.timeLabel

/**
 * One transaction as a row of a group: tap to edit, swipe left to delete. The avatar carries the
 * category's colour and the amount the direction; [index] and [count] round only the outer corners.
 *
 * The dismiss state is a plain [remember], not the saveable `rememberSwipeToDismissBoxState`: a lazy
 * list keeps a departed key's state, so an undone delete would come back dismissed and delete itself.
 */
@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = Category.of(transaction.category)
    val isInflow = transaction.type == TransactionType.INFLOW
    val accent = flowColor(isInflow)
    val shape = OutflowShapes.item(index, count)
    val threshold = SwipeToDismissBoxDefaults.positionalThreshold
    var showDialog by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    @Suppress("DEPRECATION")
    val state = remember {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            density = density,
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    showDialog = true
                    false
                } else {
                    true
                }
            },
            positionalThreshold = threshold
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete transaction?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = { if (it == SwipeToDismissBoxValue.EndToStart) onDelete() }
    ) {
        SegmentedListItem(
            onClick = onClick,
            shapes = OutflowShapes.segmented(index, count),
            colors = OutflowColors.listItem,
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(category.container),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.label,
                        tint = category.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            supportingContent = {
                Text(
                    text = "${category.label} · ${timeLabel(transaction.timestamp)}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Text(
                    text = signed(transaction.amount, isInflow),
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                    maxLines = 1
                )
            }
        ) {
            Text(
                text = transaction.title.ifBlank { category.label },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** A settings-style row of a group. Non-interactive when [onClick] is null, so it takes no ripple. */
@Composable
fun AppItem(
    title: String,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    supporting: @Composable (() -> Unit)? = null
) {
    val leading: (@Composable () -> Unit)? = icon?.let { { Icon(it, contentDescription = null) } }
    val body: (@Composable () -> Unit)? = supporting
        ?: description?.let { { Text(it) } }
    val headline: @Composable () -> Unit = { Text(title) }

    if (onClick == null) {
        ListItem(
            headlineContent = headline,
            modifier = modifier
                .fillMaxWidth()
                .clip(OutflowShapes.item(index, count)),
            supportingContent = body,
            leadingContent = leading,
            trailingContent = trailing,
            colors = OutflowColors.listItem
        )
    } else {
        SegmentedListItem(
            onClick = onClick,
            shapes = OutflowShapes.segmented(index, count),
            modifier = modifier.fillMaxWidth(),
            supportingContent = body,
            leadingContent = leading,
            trailingContent = trailing,
            colors = OutflowColors.listItem,
            content = headline
        )
    }
}

/** The label above a group of rows. Sits outside the group, in the accent colour. */
@Composable
fun AppHeading(
    text: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 12.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        trailing?.invoke()
    }
}
