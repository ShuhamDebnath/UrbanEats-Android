package com.shuham.urbaneats.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.util.NetworkResult
import com.shuham.urbaneats.domain.usecase.auth.LoginUseCase
import com.shuham.urbaneats.domain.usecase.validation.ValidateEmailUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    // Inject UseCases (Domain Layer)
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    // 1. The Source of Truth for State
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()


    // Channel for One-Time Effects (Navigation/Toasts)
    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    // 2. The Function to handle User Actions
    fun onAction(action: LoginAction) {

        when (action) {
            is LoginAction.OnEmailChange -> {
                _state.update { it.copy(email = action.email, emailError = null) }
            }

            is LoginAction.OnPasswordChange -> {
                _state.update { it.copy(password = action.password, passwordError = null) }
            }

            is LoginAction.OnLoginClick -> {
                submitLogin()
            }
        }
    }

    private fun submitLogin() {
        val currentState = _state.value

        // 1. Validation (Moved to Domain Logic ideally, but simplified here)
        val emailResult = validateEmailUseCase(currentState.email)
        if (!emailResult.successful) {
            _state.update { it.copy(emailError = emailResult.errorMessage) }
            return
        }

        // 2. Loading
        _state.update { it.copy(isLoading = true) }

        // 3. Network Call
        viewModelScope.launch {
            try {
                val result = loginUseCase(currentState.email, currentState.password)

                when (result) {
                    is NetworkResult.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        // Send One-Time Effect
                        _effect.send(LoginEffect.NavigateToHome)
                    }

                    is NetworkResult.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        // Send One-Time Effect
                        _effect.send(LoginEffect.ShowToast(result.message ?: "Unknown Error"))
                        Log.e("TAG", "submitLogin error: ${result.message} ")
                    }

                    is NetworkResult.Loading -> { /* handled above */
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginEffect.ShowToast("Crash: ${e.localizedMessage}"))
            }
        }
    }

}