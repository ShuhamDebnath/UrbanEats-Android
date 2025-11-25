package com.shuham.urbaneats.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.domain.repository.UserRepository
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val isPushEnabled: Boolean = true,
    val isEmailEnabled: Boolean = false,
    val isDarkTheme: Boolean = false, // In a real app, observe DataStore
    val selectedLanguage: String = "English",
    val selectedTheme: String = "system",
    val isUpdating: Boolean = false
)

class SettingsViewModel(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SettingsEffect>()
    val effect = _effect.receiveAsFlow()

    val userSession = tokenManager.getUserSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        observeTheme()
    }

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

    private fun observeTheme() {
        viewModelScope.launch {
            tokenManager.getTheme().collect { theme ->
                _state.update { it.copy(selectedTheme = theme) }
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            tokenManager.saveTheme(theme)
        }
    }

    // Update Profile Logic
    fun updateProfile(name: String, base64Image: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true) }
            // In a real app, show loading state here
            val result = userRepository.updateProfile(name, base64Image)

            _state.update { it.copy(isUpdating = false) } // Stop Loading

            when (result) {
                is NetworkResult.Success -> {
                    _effect.send(SettingsEffect.ShowToast("Profile updated successfully"))
                }
                is NetworkResult.Error -> {
                    _effect.send(SettingsEffect.ShowToast(result.message ?: "Failed to update profile"))
                }
                else -> {}
            }
        }
    }

    // Use a Channel for one-time events like "Password Changed Successfully" or Error Toast


    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true) }
            val result = userRepository.changePassword(oldPass, newPass)
            _state.update { it.copy(isUpdating = false) }
            when (result) {
                is NetworkResult.Success -> {
                    _effect.send(SettingsEffect.ShowToast("Password updated successfully"))
                }
                is NetworkResult.Error -> {
                    _effect.send(SettingsEffect.ShowToast(result.message ?: "Failed to update password"))
                }
                else -> {}
            }
        }
    }
}

sealed interface SettingsEffect {
    data class ShowToast(val message: String) : SettingsEffect
}