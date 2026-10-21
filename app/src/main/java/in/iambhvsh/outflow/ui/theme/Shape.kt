@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import `in`.iambhvsh.outflow.data.Category

val Category.shape: Shape
    @Composable get() = when (this) {
        Category.FOOD -> MaterialShapes.Cookie4Sided.toShape()
        Category.GROCERIES -> MaterialShapes.Clover4Leaf.toShape()
        Category.TRANSPORT -> MaterialShapes.Pill.toShape()
        Category.SHOPPING -> MaterialShapes.PuffyDiamond.toShape()
        Category.BILLS -> MaterialShapes.Pentagon.toShape()
        Category.RENT -> MaterialShapes.Arch.toShape()
        Category.HEALTH -> MaterialShapes.Heart.toShape()
        Category.ENTERTAINMENT -> MaterialShapes.Flower.toShape()
        Category.EDUCATION -> MaterialShapes.Gem.toShape()
        Category.GIFTS -> MaterialShapes.Sunny.toShape()
        Category.SALARY -> MaterialShapes.Gem.toShape()
        Category.BUSINESS -> MaterialShapes.Cookie6Sided.toShape()
        Category.INVESTMENT -> MaterialShapes.VerySunny.toShape()
        Category.REFUND -> MaterialShapes.ClamShell.toShape()
        Category.BONUS -> MaterialShapes.SoftBurst.toShape()
        Category.OTHER -> MaterialShapes.SoftBoom.toShape()
    }

object OutflowShapes {

    val Gap = ListItemDefaults.SegmentedGap

    val topItem: RoundedCornerShape
        @Composable get() = RoundedCornerShape(
            topStart = shapes.largeIncreased.topStart,
            topEnd = shapes.largeIncreased.topEnd,
            bottomStart = shapes.small.bottomStart,
            bottomEnd = shapes.small.bottomEnd
        )

    val middleItem: RoundedCornerShape
        @Composable get() = RoundedCornerShape(shapes.small.topStart)

    val bottomItem: RoundedCornerShape
        @Composable get() = RoundedCornerShape(
            topStart = shapes.small.topStart,
            topEnd = shapes.small.topEnd,
            bottomStart = shapes.largeIncreased.bottomStart,
            bottomEnd = shapes.largeIncreased.bottomEnd
        )

    val card: CornerBasedShape
        @Composable get() = shapes.largeIncreased

    @Composable
    fun segmented(index: Int, count: Int): ListItemShapes =
        ListItemDefaults.segmentedShapes(
            index,
            count,
            ListItemDefaults.shapes(
                shape = if (count == 1) shapes.largeIncreased else shapes.small,
                selectedShape = shapes.extraLargeIncreased,
                pressedShape = shapes.extraLargeIncreased,
                focusedShape = shapes.largeIncreased,
                hoveredShape = shapes.extraLarge,
                draggedShape = shapes.extraLargeIncreased
            )
        )

    @Composable
    fun item(index: Int, count: Int): Shape = when {
        count == 1 -> RoundedCornerShape(shapes.largeIncreased.topStart)
        index == 0 -> topItem
        index == count - 1 -> bottomItem
        else -> middleItem
    }
}
