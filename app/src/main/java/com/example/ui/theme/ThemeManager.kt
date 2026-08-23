package com.example.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val displayName: String, val subtitle: String) {
    ESPRESSO("Espresso", "Dark Velvet Roast"),
    CREAM("Cream", "Light Silky Cream"),
}

object ThemeManager {
    private val _themeMode = MutableStateFlow(AppThemeMode.ESPRESSO)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    val isDark: Boolean
        get() = _themeMode.value == AppThemeMode.ESPRESSO

    fun toggleTheme() {
        _themeMode.value = if (_themeMode.value == AppThemeMode.ESPRESSO) {
            AppThemeMode.CREAM
        } else {
            AppThemeMode.ESPRESSO
        }
    }

    fun setTheme(mode: AppThemeMode) {
        _themeMode.value = mode
    }
}
