@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iambhvsh.outflow.ui.theme.HeroFont
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** How long the sheet holds its height after the keyboard reports itself gone. */
private const val KeyboardSettle = 250L

/**
 * The one modal sheet shape the app uses: a hero title with an optional control beside it, then
 * whatever the caller puts under it, clear of the keyboard and the navigation bar.
 *
 * The content is handed a `dismiss` that any closing button must use: it plays the exit before
 * reporting through [onDismiss], where flipping the caller's flag drops the sheet mid-frame.
 */
@Composable
fun AppSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit
) {
    val state = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val scope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        scope.launch {
            state.hide()
            onDismiss()
        }
    }

    val keyboard = keyboardInset()
    val bottom = WindowInsets(bottom = with(LocalDensity.current) { keyboard.roundToPx() })
        .union(WindowInsets.navigationBars)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = state,
        containerColor = OutflowColors.sheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(bottom)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    fontFamily = HeroFont,
                    fontSize = 28.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                trailing?.invoke()
            }
            content(dismiss)
        }
    }
}

/**
 * How much room to leave for the keyboard — the live inset on the way up, a beat behind it on the way
 * down. Swapping between the decimal pad and the text one reports no keyboard for a moment, and
 * deferring only the shrink rides out that gap so the sheet does not drop and get shoved back up.
 */
@Composable
private fun keyboardInset(): Dp {
    val density = LocalDensity.current
    val target = with(density) { WindowInsets.ime.getBottom(density).toDp() }
    val settle = MaterialTheme.motionScheme.fastSpatialSpec<Dp>()
    val held = remember { Animatable(target, Dp.VectorConverter) }

    LaunchedEffect(target) {
        if (target > held.value) {
            held.snapTo(target)
        } else if (target < held.value) {
            delay(KeyboardSettle)
            held.animateTo(target, settle)
        }
    }

    return held.value
}
