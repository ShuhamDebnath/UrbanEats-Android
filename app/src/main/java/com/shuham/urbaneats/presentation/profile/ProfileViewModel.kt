package com.shuham.urbaneats.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.local.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/*
class ProfileViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _user = MutableStateFlow<UserSession?>(null)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.getUserSession().collect { session ->
                _user.value = session
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearSession()
            // Logic to navigate back to Login handled in UI
        }
    }
}
 */


class ProfileViewModel(private val tokenManager: TokenManager) : ViewModel() {

    val userSession = tokenManager.getUserSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearSession()
        }
    }
}