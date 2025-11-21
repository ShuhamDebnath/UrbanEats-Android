package com.shuham.urbaneats.domain.usecase.cart

import com.shuham.urbaneats.domain.repository.CartRepository

class RemoveFromCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: String) {
        repository.removeFromCart(productId)
    }
}