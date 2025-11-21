package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.util.NetworkResult
import com.shuham.urbaneats.domain.model.AuthRequest
import com.shuham.urbaneats.domain.model.AuthResponse


interface AuthRepository {
    suspend fun login(request: AuthRequest): NetworkResult<AuthResponse>
}