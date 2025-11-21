package com.shuham.urbaneats.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.data.local.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // We use a Boolean? (Tri-state):
    // null = Loading
    // true = Logged In
    // false = Not Logged In
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            // 1. Artificial Delay (Optional - keeps logo visible for branding)
            delay(1500)

            // 2. Check Token
            tokenManager.getToken().collect { token ->
                _isLoggedIn.value = !token.isNullOrBlank()
            }
        }
    }
}