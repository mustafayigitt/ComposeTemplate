package com.lhacenmed.budget.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val Context.appDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "theme_preferences")

object PreferenceUtil {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var dataStore: DataStore<Preferences>

    private val KEY_DARK_THEME = intPreferencesKey("dark_theme_value")
    private val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    private val KEY_COLOR_INDEX = intPreferencesKey("theme_color_index")

    private val _darkThemePreference = MutableStateFlow(DarkThemePreference())
    val darkThemePreference: StateFlow<DarkThemePreference> = _darkThemePreference

    private val _isDynamicColorEnabled = MutableStateFlow(false)
    val isDynamicColorEnabled: StateFlow<Boolean> = _isDynamicColorEnabled

    private val _themeColorIndex = MutableStateFlow(0)
    val themeColorIndex: StateFlow<Int> = _themeColorIndex

    fun init(context: Context) {
        dataStore = context.applicationContext.appDataStore
        scope.launch {
            dataStore.data.collect { prefs ->
                _darkThemePreference.value = DarkThemePreference(
                    darkThemeValue = prefs[KEY_DARK_THEME] ?: DarkThemePreference.FOLLOW_SYSTEM,
                    isHighContrastModeEnabled = prefs[KEY_HIGH_CONTRAST] ?: false,
                )
                _isDynamicColorEnabled.value = prefs[KEY_DYNAMIC_COLOR] ?: false
                _themeColorIndex.value = prefs[KEY_COLOR_INDEX] ?: 0
            }
        }
    }

    fun modifyDarkThemePreference(
        darkThemeValue: Int = _darkThemePreference.value.darkThemeValue,
        isHighContrastModeEnabled: Boolean = _darkThemePreference.value.isHighContrastModeEnabled,
    ) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_DARK_THEME] = darkThemeValue
                prefs[KEY_HIGH_CONTRAST] = isHighContrastModeEnabled
            }
        }
    }

    fun switchDynamicColor(enabled: Boolean = !_isDynamicColorEnabled.value) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_DYNAMIC_COLOR] = enabled
            }
        }
    }

    fun modifyThemeColor(index: Int) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_COLOR_INDEX] = index
            }
        }
    }
}
