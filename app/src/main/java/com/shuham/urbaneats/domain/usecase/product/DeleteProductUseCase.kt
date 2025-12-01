package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.util.NetworkResult

class DeleteProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: String): NetworkResult<Unit> {
        return repository.deleteProduct(productId)
    }
}