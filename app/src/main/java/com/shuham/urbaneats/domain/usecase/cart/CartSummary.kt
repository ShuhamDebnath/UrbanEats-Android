package com.shuham.urbaneats.domain.usecase.cart

import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CartSummary(
    val items: List<CartItemEntity>,
    val totalPrice: Double
)

class GetCartUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<CartSummary> {
        return repository.getCartItems().map { items ->
            val total = items.sumOf { it.price * it.quantity }
            CartSummary(items, total)
        }
    }
}