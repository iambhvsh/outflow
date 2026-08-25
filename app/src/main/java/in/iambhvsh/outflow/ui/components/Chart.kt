@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import `in`.iambhvsh.outflow.ui.Day
import `in`.iambhvsh.outflow.ui.utils.axis
import `in`.iambhvsh.outflow.ui.utils.money
import `in`.iambhvsh.outflow.ui.utils.weekdayLabel

private val PlotHeight = 200.dp
private val AxisGap = 8.dp
private val ColumnGap = 4.dp

/** A week's worth of slots, so a week with three days in it still draws day-wide columns. */
private const val Slots = 7

/** Column width, whatever the slot's width, clamped to the slot on a screen too tight for it. */
private val ColumnThickness = 28.dp

/** Intervals the value axis is divided into, so it carries four labels counting zero. */
private const val Steps = 3

private val MarkerLift = 26.dp
private val MarkerHeight = 28.dp
private val MarkerDrop = 6.dp

/**
 * A column per day, scaled to the busiest one, drawn in the accent at or above the week's average and
 * in the secondary below it. Tap a column for its figures, income included.
 */
@Composable
fun DayChart(
    days: List<Day>,
    modifier: Modifier = Modifier,
    height: Dp = PlotHeight
) {
    if (days.isEmpty()) return

    val peak = days.maxOf { it.summary.outflow }
    val average = days.sumOf { it.summary.outflow } / days.size
    val ceiling = peak.coerceAtLeast(1.0)
    val spare = (Slots - days.size).coerceAtLeast(0)
    var selected by remember(days.size) { mutableIntStateOf(-1) }
    var plot by remember { mutableStateOf(IntRect.Zero) }

    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.height(height),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (tick in Steps downTo 0) {
                Text(
                    text = axis(peak * tick / Steps),
                    modifier = Modifier
                        .height(0.dp)
                        .wrapContentHeight(unbounded = true),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.width(AxisGap))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .onGloballyPositioned { plot = it.boundsInWindow().roundToIntRect() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(ColumnGap),
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEachIndexed { index, day ->
                        val interaction = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(interactionSource = interaction, indication = null) {
                                    selected = if (selected == index) -1 else index
                                },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Pillar(
                                share = day.summary.outflow / ceiling,
                                above = day.summary.outflow >= average,
                                marker = readout(day).takeIf { selected == index },
                                plot = height,
                                bounds = plot
                            )
                        }
                    }
                    repeat(spare) { Spacer(Modifier.weight(1f)) }
                }
            }

            Spacer(Modifier.height(AxisGap))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ColumnGap)
            ) {
                days.forEach { day ->
                    Text(
                        text = weekdayLabel(day.timestamp).take(1),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
                repeat(spare) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private fun readout(day: Day): String = when {
    day.summary.inflow > 0.0 -> "${money(day.summary.outflow)} out · ${money(day.summary.inflow)} in"
    else -> money(day.summary.outflow)
}

/**
 * One day's spending, as a pill rising from the baseline. The marker hangs above it while the [plot]
 * has room and drops inside its head when it does not, so the tallest column keeps it off the heading.
 */
@Composable
private fun Pillar(share: Double, above: Boolean, marker: String?, plot: Dp, bounds: IntRect) {
    val fraction by animateFloatAsState(
        targetValue = share.toFloat().coerceIn(0f, 1f),
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "Pillar"
    )
    val headroom = plot * (1f - fraction)
    val lift = with(LocalDensity.current) {
        if (headroom >= MarkerLift + MarkerHeight) {
            -MarkerLift.roundToPx()
        } else {
            MarkerDrop.roundToPx()
        }
    }
    val position = remember(lift, bounds) { MarkerPosition(lift, bounds) }

    Box(
        modifier = Modifier
            .widthIn(max = ColumnThickness)
            .fillMaxWidth()
            .fillMaxHeight(fraction)
            .clip(RoundedCornerShape(percent = 50))
            .background(
                if (above) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
    ) {
        if (marker == null) return@Box
        Popup(popupPositionProvider = position) {
            Text(
                text = marker,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                maxLines = 1
            )
        }
    }
}

/** Centres a marker over its column, then nudges it back inside [bounds] so it clears the labels. */
private class MarkerPosition(private val lift: Int, private val bounds: IntRect) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val centred = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val furthest = bounds.right - popupContentSize.width
        val x = if (furthest < bounds.left) centred else centred.coerceIn(bounds.left, furthest)
        return IntOffset(x, anchorBounds.top + lift)
    }
}

