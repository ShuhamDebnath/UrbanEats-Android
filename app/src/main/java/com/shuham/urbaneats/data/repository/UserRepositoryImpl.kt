package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.domain.model.Address
import com.shuham.urbaneats.domain.repository.UserRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
data class AddAddressRequest(val userId: String, val label: String, val fullAddress: String)

class UserRepositoryImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun getAddresses(): NetworkResult<List<Address>> {
        return try {
            val userId = tokenManager.getUserSession().first().id ?: return NetworkResult.Error("Not Logged In")

            val response = client.get("api/user/address") {
                parameter("userId", userId)
            }

            if (response.status == HttpStatusCode.OK) {
                NetworkResult.Success(response.body())
            } else {
                NetworkResult.Error("Failed to load addresses")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.message}")
        }
    }

    override suspend fun addAddress(label: String, fullAddress: String): NetworkResult<List<Address>> {
        return try {
            val userId = tokenManager.getUserSession().first().id ?: return NetworkResult.Error("Not Logged In")

            val request = AddAddressRequest(userId, label, fullAddress)
            val response = client.post("api/user/address") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                NetworkResult.Success(response.body()) // Returns updated list
            } else {
                NetworkResult.Error("Failed to add address")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.message}")
        }
    }

    override suspend fun deleteAddress(addressId: String): NetworkResult<List<Address>> {
        return try {
            val userId = tokenManager.getUserSession().first().id ?: return NetworkResult.Error("Not Logged In")

            val response = client.delete("api/user/address/$addressId") {
                parameter("userId", userId)
            }

            if (response.status == HttpStatusCode.OK) {
                NetworkResult.Success(response.body())
            } else {
                NetworkResult.Error("Failed to delete address")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.message}")
        }
    }

    // Helper to set selected address
    override suspend fun selectAddress(addressId: String) {
        tokenManager.saveSelectedAddress(addressId)
    }

    override fun getSelectedAddressId(): Flow<String?> {
        return tokenManager.getSelectedAddressId()
    }

    // NEW: Implementation
    override suspend fun getAddressById(id: String): Address? {
        // Ideally, we should have a local cache of addresses.
        // For now, we fetch the list from the API and find the match.
        val result = getAddresses()
        return if (result is NetworkResult.Success) {
            result.data?.find { it.id == id }
        } else {
            null
        }
    }
}