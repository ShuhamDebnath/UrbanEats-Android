package com.shuham.urbaneats.presentation.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.local.UserSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminProfileViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

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