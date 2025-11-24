package com.shuham.urbaneats.domain.usecase.cart

import com.shuham.urbaneats.domain.repository.CartRepository

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke() {
        repository.clearCart()
    }
}