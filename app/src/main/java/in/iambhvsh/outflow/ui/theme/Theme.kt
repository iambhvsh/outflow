@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.materialkolor.rememberDynamicColorScheme
import `in`.iambhvsh.outflow.data.ThemeColor
import `in`.iambhvsh.outflow.data.ThemeMode

val ThemeColor.seed: Color
    get() = when (this) {
        ThemeColor.RED -> Color(0xFFEA4335)
        ThemeColor.BLUE -> Color(0xFF4285F4)
        ThemeColor.GREEN -> Color(0xFF34A853)
        ThemeColor.YELLOW -> Color(0xFFFBBC04)
        ThemeColor.ORANGE -> Color(0xFFFF9800)
        ThemeColor.PURPLE -> Color(0xFF9C27B0)
        ThemeColor.PINK -> Color(0xFFE91E63)
        ThemeColor.TEAL -> Color(0xFF009688)
    }

@Composable
fun OutflowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    themeColor: ThemeColor = ThemeColor.BLUE,
    isDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.OLED -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val isBlack = themeMode == ThemeMode.OLED
    OutflowColors.isBlack = isBlack

    val context = LocalContext.current
    val view = LocalView.current

    DisposableEffect(isDark, view) {
        if (!view.isInEditMode) {
            val window = (context as? ComponentActivity)?.window
            if (window != null) {
                val insets = WindowCompat.getInsetsController(window, view)
                insets.isAppearanceLightStatusBars = !isDark
                insets.isAppearanceLightNavigationBars = !isDark
            }
        }
        onDispose {}
    }

    val colorScheme = if (isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamic = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        if (isBlack) dynamic.copy(background = Color.Black, surface = Color.Black) else dynamic
    } else {
        rememberDynamicColorScheme(
            seedColor = themeColor.seed,
            isDark = isDark,
            isAmoled = isBlack
        )
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = OutflowTypography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
