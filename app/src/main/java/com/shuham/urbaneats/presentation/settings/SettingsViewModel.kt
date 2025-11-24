package com.shuham.urbaneats.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val isPushEnabled: Boolean = true,
    val isEmailEnabled: Boolean = false,
    val isDarkTheme: Boolean = false, // In a real app, observe DataStore
    val selectedLanguage: String = "English"
)

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun togglePush(enabled: Boolean) {
        _state.update { it.copy(isPushEnabled = enabled) }
        // TODO: Save to DataStore/Server
    }

    fun toggleEmail(enabled: Boolean) {
        _state.update { it.copy(isEmailEnabled = enabled) }
    }

    fun toggleTheme(enabled: Boolean) {
        _state.update { it.copy(isDarkTheme = enabled) }
        // TODO: Update App Theme
    }
}