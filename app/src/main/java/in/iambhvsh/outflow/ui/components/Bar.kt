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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes
import `in`.iambhvsh.outflow.ui.utils.money
import `in`.iambhvsh.outflow.ui.utils.percent

/** One band of a [StackedBar]. */
data class Segment(val label: String, val value: Double, val color: Color)

/** How far a band's tooltip floats above it. */
private val TooltipLift = 28.dp

/**
 * A single bar split into proportional bands, each reporting itself in a tooltip when tapped. Bands
 * keep a floor share so a 0.4% slice is still something you can hit, and only the bar's outer ends
 * take the large corner, so the bands read as one object that has been cut.
 */
@Composable
fun StackedBar(
    segments: List<Segment>,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp
) {
    val visible = segments.filter { it.value > 0.0 }
    val total = visible.sumOf { it.value }
    if (visible.isEmpty() || total <= 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        return
    }

    var selected by remember(visible.size) { mutableIntStateOf(-1) }
    val large = MaterialTheme.shapes.large
    val small = MaterialTheme.shapes.extraSmall
    val lift = with(LocalDensity.current) { -TooltipLift.roundToPx() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        visible.forEachIndexed { index, segment ->
            val share = (segment.value / total).toFloat()
            val weight by animateFloatAsState(
                targetValue = share.coerceAtLeast(0.02f),
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                label = segment.label
            )
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(weight)
                    .fillMaxHeight()
                    .clip(
                        when {
                            visible.size == 1 -> large
                            index == 0 -> large.copy(topEnd = small.topEnd, bottomEnd = small.bottomEnd)
                            index == visible.lastIndex ->
                                large.copy(topStart = small.topStart, bottomStart = small.bottomStart)

                            else -> small
                        }
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .background(segment.color)
                    .clickable(interactionSource = interaction, indication = null) {
                        selected = if (selected == index) -1 else index
                    }
            ) {
                if (selected != index) return@Box
                Popup(alignment = Alignment.TopCenter, offset = IntOffset(0, lift)) {
                    Text(
                        text = "${segment.label} · ${money(segment.value)} · ${percent(segment.value, total)}",
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.inverseSurface,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** The list of names, amounts and shares behind a [StackedBar]. */
@Composable
fun BarLegend(
    segments: List<Segment>,
    modifier: Modifier = Modifier
) {
    val visible = segments.filter { it.value > 0.0 }
    val total = visible.sumOf { it.value }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        visible.forEach { segment ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(segment.color)
                )
                Text(
                    text = segment.label,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = percent(segment.value, total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = money(segment.value),
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
