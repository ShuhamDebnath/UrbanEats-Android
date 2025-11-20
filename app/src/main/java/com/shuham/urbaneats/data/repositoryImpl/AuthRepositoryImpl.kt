package com.shuham.urbaneats.data.repositoryImpl

import com.shuham.urbaneats.data.remote.NetworkResult
import com.shuham.urbaneats.domain.model.AuthRequest
import com.shuham.urbaneats.domain.model.AuthResponse
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
}