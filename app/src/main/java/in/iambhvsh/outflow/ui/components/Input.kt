@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay

/** How long to keep re-asking for the keyboard after a field takes focus, and how often. */
private const val KeyboardHolds = 3
private const val KeyboardHoldDelay = 80L

/**
 * The one text field shape the app uses. Each field re-asserts the keyboard while it holds focus:
 * moving from the amount to the title swaps the decimal pad for a text one, and that teardown reads to
 * many keyboards as a cue to close. Asking again over the next few frames rides it out, and is a no-op
 * once the keyboard is already up.
 */
@Composable
fun AppInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var focused by remember { mutableStateOf(false) }

    LaunchedEffect(focused) {
        if (!focused) return@LaunchedEffect
        keyboard?.show()
        repeat(KeyboardHolds) {
            delay(KeyboardHoldDelay)
            keyboard?.show()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        label = { Text(label) },
        prefix = prefix?.let { { Text(it) } },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        shape = MaterialTheme.shapes.large
    )
}
