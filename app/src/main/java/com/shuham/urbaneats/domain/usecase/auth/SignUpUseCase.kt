package com.shuham.urbaneats.domain.usecase.auth

import com.shuham.urbaneats.domain.model.AuthRequest
import com.shuham.urbaneats.domain.model.AuthResponse
import com.shuham.urbaneats.domain.repository.AuthRepository
import com.shuham.urbaneats.util.NetworkResult

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, pass: String): NetworkResult<AuthResponse> {
        // Add simple validation if needed, or rely on repo
        return repository.register(name, email, pass)
    }
}