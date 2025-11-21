package com.shuham.urbaneats.domain.usecase.cart

import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.CartRepository

class AddToCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(product: Product) {
        repository.addToCart(product)
    }
}