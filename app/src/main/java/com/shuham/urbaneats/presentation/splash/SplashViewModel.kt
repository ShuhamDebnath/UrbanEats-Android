package com.shuham.urbaneats.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.local.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashDestination {
    data object Loading : SplashDestination
    data object Login : SplashDestination
    data object Home : SplashDestination
    data object Admin : SplashDestination
}

class SplashViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination = _destination.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            delay(1500)
            val session = tokenManager.getUserSession().first()

            if (session.token.isNullOrBlank()) {
                _destination.value = SplashDestination.Login
            } else {
                if (session.role == "admin") {
                    _destination.value = SplashDestination.Admin
                } else {
                    _destination.value = SplashDestination.Home
                }
            }
        }
    }
}