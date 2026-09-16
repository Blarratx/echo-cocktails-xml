package com.example.myapy.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "echo_settings")

class ThemeManager(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("app_theme")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val AVATAR_KEY = stringPreferencesKey("user_avatar")

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        val themeName = prefs[THEME_KEY] ?: ThemeType.PANDORA.name
        AppTheme.getTheme(ThemeType.valueOf(themeName))
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY]
    }

    val avatarFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[AVATAR_KEY]?.toInt() ?: 0 // 0 will be our default avatar index
    }

    suspend fun saveTheme(themeType: ThemeType) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = themeType.name
        }
    }

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = name
        }
    }

    suspend fun saveAvatar(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[AVATAR_KEY] = index.toString()
        }
    }
}
