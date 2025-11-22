package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.repository.ProductRepository

class ToggleFavoriteUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: String, isFavorite: Boolean) {
        repository.toggleFavorite(productId, isFavorite)
    }
}