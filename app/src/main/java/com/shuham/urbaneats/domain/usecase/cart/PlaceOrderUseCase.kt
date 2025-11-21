package com.shuham.urbaneats.domain.usecase.cart

import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.domain.repository.CartRepository
import com.shuham.urbaneats.util.NetworkResult

class PlaceOrderUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(address: String, total: Double, items: List<CartItemEntity>): NetworkResult<Unit> {
        if (address.isBlank()) return NetworkResult.Error("Address is required")
        if (items.isEmpty()) return NetworkResult.Error("Cart is empty")

        return repository.placeOrder(address, total, items)
    }
}