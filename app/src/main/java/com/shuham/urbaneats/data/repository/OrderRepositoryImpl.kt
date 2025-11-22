package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.mapper.toDomain
import com.shuham.urbaneats.data.remote.dto.OrderResponseDto
import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.domain.repository.OrderRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first

class OrderRepositoryImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) : OrderRepository {

    override suspend fun getMyOrders(): NetworkResult<List<Order>> {
        return try {
            // Get User ID safely
            val session = tokenManager.getUserSession().first()
            val userId = session.id ?: return NetworkResult.Error("User not logged in")

            val response = client.get("api/orders/$userId")

            if (response.status == HttpStatusCode.OK) {
                val orders = response.body<List<OrderResponseDto>>()
                NetworkResult.Success(orders.map { it.toDomain() })
            } else {
                NetworkResult.Error("Failed to fetch orders")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.localizedMessage}")
        }
    }
}