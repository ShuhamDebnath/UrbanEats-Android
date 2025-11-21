package com.shuham.urbaneats.domain.usecase.cart

import com.shuham.urbaneats.domain.repository.CartRepository

class UpdateCartQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: String, newQuantity: Int) {
        repository.updateQuantity(productId, newQuantity)
    }
}