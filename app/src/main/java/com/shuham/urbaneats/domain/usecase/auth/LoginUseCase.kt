package com.shuham.urbaneats.domain.usecase.auth

import com.shuham.urbaneats.util.NetworkResult
import com.shuham.urbaneats.domain.model.AuthRequest
import com.shuham.urbaneats.domain.model.AuthResponse
import com.shuham.urbaneats.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    // We pass individual strings to keep the ViewModel clean
    suspend operator fun invoke(email: String, pass: String): NetworkResult<AuthResponse> {
        // You could do extra logic here, like hashing the password before sending
        val request = AuthRequest(email = email, password = pass)
        return repository.login(request)
    }
}