package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.ProductRepository

class GetProductDetailsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: String): Product? {
        return repository.getProductById(id)
    }
}