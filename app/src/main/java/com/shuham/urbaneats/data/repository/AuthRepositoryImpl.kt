package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.util.NetworkResult
import com.shuham.urbaneats.domain.model.AuthRequest
import com.shuham.urbaneats.domain.model.AuthResponse
import com.shuham.urbaneats.domain.model.RegisterRequest
import com.shuham.urbaneats.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {

    override suspend fun login(request: AuthRequest): NetworkResult<AuthResponse> {
        return try {
            // Actual Network Call
            val response = client.post("api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                val data = response.body<AuthResponse>()
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error("Login Failed: ${response.status}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.message}")
        }
    }


    override suspend fun register(name: String, email: String, pass: String): NetworkResult<AuthResponse> {
        return try {
            val request = RegisterRequest(name, email, pass)

            // Backend Route: backend/routes/auth.js -> router.post('/register', ...)
            val response = client.post("api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                // Some backends return just the user, some return token + user.
                // Our Day 3 backend code returned: res.send({ user: savedUser._id });
                // BUT to auto-login, we need the token.

                // CRITICAL FIX: If your backend only returns ID, we can't auto-login easily.
                // Ideally, the backend should return the same structure as login (Token + User).
                // If it returns AuthResponse, this works.
                // If it throws parsing error, we need to update the Backend to return the token on register too.

                // Assuming you updated backend or it returns compatible JSON:
                try {
                    val data = response.body<AuthResponse>()
                    NetworkResult.Success(data)
                } catch (e: Exception) {
                    // If backend only returns ID, we might treat it as success but require login
                    // For now, let's assume we want smooth auto-login, so the backend must send the token.
                    NetworkResult.Error("Registration successful, but response format mismatch.")
                }
            } else {
                NetworkResult.Error("Registration Failed: ${response.status.value}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.localizedMessage}")
        }
    }
}