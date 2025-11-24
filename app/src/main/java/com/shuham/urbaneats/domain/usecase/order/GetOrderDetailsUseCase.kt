package com.shuham.urbaneats.domain.usecase.order

import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.domain.repository.OrderRepository
import com.shuham.urbaneats.util.NetworkResult

class GetOrderDetailsUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String): NetworkResult<Order> {
        return repository.getOrderById(orderId)
    }
}