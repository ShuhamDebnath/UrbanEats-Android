package com.shuham.urbaneats.domain.usecase.order

import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.domain.repository.OrderRepository
import com.shuham.urbaneats.util.NetworkResult


class GetAllOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(): NetworkResult<List<Order>> {
        return repository.getAllOrders()
    }
}