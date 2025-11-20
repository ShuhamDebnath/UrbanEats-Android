package com.shuham.urbaneats.presentation.login


// UI Events (Actions from User)
sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object OnLoginClick : LoginAction
}