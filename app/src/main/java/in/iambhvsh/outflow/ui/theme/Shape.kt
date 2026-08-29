@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object OutflowShapes {

    val Gap = ListItemDefaults.SegmentedGap

    val topItem: RoundedCornerShape
        @Composable get() = RoundedCornerShape(
            topStart = shapes.large.topStart,
            topEnd = shapes.large.topEnd,
            bottomStart = shapes.extraSmall.bottomStart,
            bottomEnd = shapes.extraSmall.bottomEnd
        )

    val middleItem: CornerBasedShape
        @Composable get() = shapes.extraSmall

    val bottomItem: RoundedCornerShape
        @Composable get() = RoundedCornerShape(
            topStart = shapes.extraSmall.topStart,
            topEnd = shapes.extraSmall.topEnd,
            bottomStart = shapes.large.bottomStart,
            bottomEnd = shapes.large.bottomEnd
        )

    val card: CornerBasedShape
        @Composable get() = shapes.large

    @Composable
    fun segmented(index: Int, count: Int): ListItemShapes =
        ListItemDefaults.segmentedShapes(
            index,
            count,
            ListItemDefaults.shapes(
                shape = if (count == 1) shapes.large else shapes.extraSmall,
                selectedShape = shapes.extraLargeIncreased,
                pressedShape = shapes.extraLargeIncreased,
                focusedShape = shapes.large,
                hoveredShape = shapes.extraLarge,
                draggedShape = shapes.extraLargeIncreased
            )
        )

    @Composable
    fun item(index: Int, count: Int): Shape = when {
        count == 1 -> card
        index == 0 -> topItem
        index == count - 1 -> bottomItem
        else -> middleItem
    }
}
