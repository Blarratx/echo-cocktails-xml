package com.example.myapy.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "echo_settings")

class ThemeManager(private val context: Context) {
    private val themeKey = stringPreferencesKey("app_theme")
    private val usernameKey = stringPreferencesKey("username")
    private val avatarKey = stringPreferencesKey("user_avatar")

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        val themeName = prefs[themeKey] ?: ThemeType.PANDORA.name
        val themeType = runCatching { ThemeType.valueOf(themeName) }.getOrDefault(ThemeType.PANDORA)
        AppTheme.getTheme(themeType)
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[usernameKey]
    }

    val avatarFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[avatarKey]?.toIntOrNull() ?: 0
    }

    suspend fun saveTheme(themeType: ThemeType) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = themeType.name
        }
    }

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { prefs ->
            prefs[usernameKey] = name
        }
    }

    suspend fun saveAvatar(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[avatarKey] = index.toString()
        }
    }
}
