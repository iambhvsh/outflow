package `in`.iambhvsh.outflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    SYSTEM, LIGHT, DARK, OLED
}

enum class ThemeColor {
    RED, BLUE, GREEN, YELLOW, ORANGE, PURPLE, PINK, TEAL
}

/** The currencies figures can be printed in, in the order the picker offers them. */
enum class Currency(val symbol: String, val label: String) {
    INR("₹", "Indian rupee"),
    USD("$", "US dollar"),
    EUR("€", "Euro"),
    YEN("¥", "Japanese yen")
}

/** Everything the app remembers between launches. */
data class Options(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeColor: ThemeColor = ThemeColor.BLUE,
    val isDynamicColor: Boolean = false,
    val currency: Currency = Currency.INR
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * The stored settings, read as one flow of one object rather than a flow per setting: nothing can be
 * drawn until the first read off disk lands, and one value means one arrival and one frame.
 */
class PreferencesManager(private val context: Context) {

    companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
        val IS_DYNAMIC_COLOR_KEY = booleanPreferencesKey("is_dynamic_color")
        val CURRENCY_KEY = stringPreferencesKey("currency")
    }

    val options: Flow<Options> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { stored ->
            Options(
                themeMode = stored[THEME_MODE_KEY].orElse(ThemeMode.SYSTEM),
                themeColor = stored[THEME_COLOR_KEY].orElse(ThemeColor.BLUE),
                isDynamicColor = stored[IS_DYNAMIC_COLOR_KEY] ?: false,
                currency = stored[CURRENCY_KEY].orElse(Currency.INR)
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
}

/**
 * Reads a stored enum name, falling back to [fallback] for anything missing or unrecognised, so a
 * value written by a build with more options than this one reverts rather than crashing.
 */
private inline fun <reified T : Enum<T>> String?.orElse(fallback: T): T =
    this?.let { stored -> enumValues<T>().firstOrNull { it.name == stored } } ?: fallback
