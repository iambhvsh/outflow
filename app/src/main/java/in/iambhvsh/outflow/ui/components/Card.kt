@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes

/**
 * A row of a group holding arbitrary content rather than a list item — a chart, a hero figure, a
 * legend. Set [bleed] for content that should run out to the card's trailing edge.
 */
@Composable
fun AppCard(
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    title: String? = null,
    bleed: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(OutflowShapes.item(index, count))
            .background(OutflowColors.row)
            .padding(
                PaddingValues(
                    start = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp,
                    end = if (bleed) 0.dp else 20.dp
                )
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = if (bleed) 20.dp else 0.dp)
            )
        }
        content()
    }
}
