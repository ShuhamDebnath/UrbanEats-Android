package com.shuham.urbaneats.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.domain.usecase.auth.SignUpUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface SignUpEffect {
    data object NavigateToHome : SignUpEffect
    data class ShowToast(val message: String) : SignUpEffect
}

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SignUpEffect>()
    val effect = _effect.receiveAsFlow()

    fun onNameChange(newValue: String) {
        _state.update { it.copy(name = newValue) }
    }

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun onRegisterClick() {
        val currentState = _state.value

        if (currentState.name.isBlank() || currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(error = "All fields are required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = signUpUseCase(currentState.name, currentState.email, currentState.password)

            when (result) {
                is NetworkResult.Success -> {
                    // Save Session
                    result.data?.let { auth ->
                        tokenManager.saveSession(
                            token = auth.token,
                            id = auth.user.id,
                            name = auth.user.name,
                            email = auth.user.email
                        )
                    }
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(SignUpEffect.NavigateToHome)
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                    _effect.send(SignUpEffect.ShowToast(result.message ?: "Registration Failed"))
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}