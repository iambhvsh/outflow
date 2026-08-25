@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iambhvsh.outflow.ui.Screen

/**
 * The bottom navigation: a vibrant floating toolbar of toggle buttons, the add-flow button last and set
 * apart so it does not read as a fourth tab. Only the selected destination shows its label.
 *
 * Fixed, with no scroll behaviour — one that hides on scroll strands itself at the end of a list. Where
 * it sits is the caller's business, as padding rather than an offset so the scaffold reserves the space.
 */
@Composable
fun AppNavigation(
    current: Screen,
    onSelect: (Screen) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val motion = MaterialTheme.motionScheme

    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    ) {
        Screen.entries.forEach { screen ->
            val selected = screen == current
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above
                ),
                tooltip = { PlainTooltip { Text(screen.label) } },
                state = rememberTooltipState()
            ) {
                ToggleButton(
                    checked = selected,
                    onCheckedChange = { if (!selected) onSelect(screen) },
                    modifier = Modifier.height(56.dp),
                    shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                        checkedContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Crossfade(selected) { active ->
                            Icon(
                                imageVector = if (active) screen.active else screen.icon,
                                contentDescription = screen.label
                            )
                        }
                        AnimatedVisibility(
                            visible = selected,
                            enter = expandHorizontally(motion.defaultSpatialSpec()),
                            exit = shrinkHorizontally(motion.defaultSpatialSpec())
                        ) {
                            Text(
                                text = screen.label,
                                modifier = Modifier.padding(start = ButtonDefaults.IconSpacing),
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        FilledIconButton(
            onClick = onAdd,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "New flow")
        }
    }
}
