package com.shuham.urbaneats.presentation.login

// Side Effects (One-time Events for UI to handle)
sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data class ShowToast(val message: String) : LoginEffect
}