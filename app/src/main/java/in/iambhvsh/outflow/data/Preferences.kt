package `in`.iambhvsh.outflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    SYSTEM, LIGHT, DARK, OLED
}

enum class ThemeColor {
    RED, BLUE, GREEN, YELLOW, ORANGE, PURPLE, PINK, TEAL
}

enum class Currency(val symbol: String, val label: String) {
    INR("₹", "Indian rupee"),
    USD("$", "US dollar"),
    EUR("€", "Euro"),
    YEN("¥", "Japanese yen")
}

data class Options(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeColor: ThemeColor = ThemeColor.BLUE,
    val isDynamicColor: Boolean = false,
    val currency: Currency = Currency.INR,
    val isAutoSmsTracking: Boolean = false
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
        val IS_DYNAMIC_COLOR_KEY = booleanPreferencesKey("is_dynamic_color")
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val IS_AUTO_SMS_TRACKING_KEY = booleanPreferencesKey("is_auto_sms_tracking")
    }

    val options: Flow<Options> = context.dataStore.data.map { stored ->
        Options(
            themeMode = stored[THEME_MODE_KEY].orElse(ThemeMode.SYSTEM),
            themeColor = stored[THEME_COLOR_KEY].orElse(ThemeColor.BLUE),
            isDynamicColor = stored[IS_DYNAMIC_COLOR_KEY] ?: false,
            currency = stored[CURRENCY_KEY].orElse(Currency.INR),
            isAutoSmsTracking = stored[IS_AUTO_SMS_TRACKING_KEY] ?: false
        )
    }

    suspend fun saveThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun saveThemeColor(color: ThemeColor) {
        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR_KEY] = color.name
        }
    }

    suspend fun saveIsDynamicColor(isDynamic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLOR_KEY] = isDynamic
        }
    }

    suspend fun saveCurrency(currency: Currency) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency.name
        }
    }

    suspend fun saveIsAutoSmsTracking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_AUTO_SMS_TRACKING_KEY] = enabled
        }
    }

    suspend fun restoreOptions(options: Options) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = options.themeMode.name
            preferences[THEME_COLOR_KEY] = options.themeColor.name
            preferences[IS_DYNAMIC_COLOR_KEY] = options.isDynamicColor
            preferences[CURRENCY_KEY] = options.currency.name
            preferences[IS_AUTO_SMS_TRACKING_KEY] = options.isAutoSmsTracking
        }
    }
}

private inline fun <reified T : Enum<T>> String?.orElse(fallback: T): T =
    this?.let { stored -> enumValues<T>().firstOrNull { it.name == stored } } ?: fallback
