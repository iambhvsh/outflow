@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** How far apart tiles sit. Wider than the 2dp a group's rows use — see [TileRow]. */
private val TileGap = 8.dp

/**
 * Two or three tiles across, sharing the row evenly. Tiles are separate cards rather than rows of one
 * group, so they sit further apart than 2dp, where the two would read as one slab split by a hairline.
 */
@Composable
fun TileRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TileGap)
    ) {
        content()
    }
}

/**
 * A single figure with its label, on a filled slab. The figure shrinks to fit rather than wrapping, so
 * a tile stays legible whether it holds `$8` or `$128,400.75`.
 */
@Composable
fun RowScope.AppTile(
    value: String,
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.largeIncreased)
            .background(container)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            modifier = Modifier.fillMaxWidth(),
            autoSize = TextAutoSize.StepBased(minFontSize = 18.sp, maxFontSize = 34.sp),
            maxLines = 1,
            style = MaterialTheme.typography.displaySmall.copy(color = content)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
