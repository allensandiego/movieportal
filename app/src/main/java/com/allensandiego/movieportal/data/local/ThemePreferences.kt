package com.allensandiego.movieportal.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(getThemePreference())
    val theme: StateFlow<AppTheme> = _theme

    private fun getThemePreference(): AppTheme {
        val themeName = sharedPreferences.getString("selected_theme", AppTheme.SYSTEM.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.SYSTEM.name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    fun setTheme(theme: AppTheme) {
        sharedPreferences.edit().putString("selected_theme", theme.name).apply()
        _theme.value = theme
    }
}
