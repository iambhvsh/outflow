@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.theme

import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import `in`.iambhvsh.outflow.data.Category

private val InflowLight = Color(0xFF1B6C33)
private val InflowDark = Color(0xFF7FDB97)
private val OutflowLight = Color(0xFFB3261E)
private val OutflowDark = Color(0xFFFFB4AB)


private const val CategoryChroma = 46.0
private const val CategoryToneLight = 42.0
private const val CategoryToneDark = 80.0


object OutflowColors {


    var isBlack by mutableStateOf(false)
        internal set


    val group: Color
        @Composable get() = if (isBlack) colorScheme.surface else colorScheme.surfaceContainer


    val row: Color
        @Composable get() = if (isBlack) colorScheme.surfaceContainerHigh else colorScheme.surfaceBright


    val sheet: Color
        @Composable get() = if (isDark) {
            colorScheme.surfaceContainerLowest
        } else {
            colorScheme.surfaceContainerHigh
        }

    val topBar: TopAppBarColors
        @Composable get() = TopAppBarDefaults.topAppBarColors(
            containerColor = group,
            scrolledContainerColor = group
        )

    val listItem: ListItemColors
        @Composable get() = ListItemDefaults.segmentedColors(containerColor = row)

    val switch: SwitchColors
        @Composable get() = SwitchDefaults.colors(
            checkedTrackColor = colorScheme.primary,
            checkedThumbColor = colorScheme.onPrimary,
            checkedIconColor = colorScheme.primary,
            uncheckedTrackColor = colorScheme.surfaceContainerHighest,
            uncheckedBorderColor = colorScheme.outline,
            uncheckedThumbColor = colorScheme.outline,
            uncheckedIconColor = colorScheme.surfaceContainerHighest
        )


    val inflow: Color
        @Composable get() = if (isDark) InflowDark else InflowLight


    val outflow: Color
        @Composable get() = if (isDark) OutflowDark else OutflowLight


    internal val isDark: Boolean
        @Composable get() = colorScheme.surface.luminance() < 0.5f
}


@Composable
fun flowColor(positive: Boolean): Color = if (positive) OutflowColors.inflow else OutflowColors.outflow


val Category.color: Color
    @Composable get() = if (this == Category.OTHER) {
        colorScheme.onSurfaceVariant
    } else {
        Hct.from(
            hue,
            CategoryChroma,
            if (OutflowColors.isDark) CategoryToneDark else CategoryToneLight
        ).toColor()
    }


val Category.container: Color
    @Composable get() = color.copy(alpha = if (OutflowColors.isDark) 0.20f else 0.14f)
