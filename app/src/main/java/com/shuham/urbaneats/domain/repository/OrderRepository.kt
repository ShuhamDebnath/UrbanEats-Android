package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.util.NetworkResult

interface OrderRepository {
    suspend fun getMyOrders(): NetworkResult<List<Order>>
    suspend fun getOrderById(orderId: String): NetworkResult<Order>
}