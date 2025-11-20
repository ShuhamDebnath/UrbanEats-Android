package com.shuham.urbaneats.presentation.login



// UI State (Persistent Data)
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)