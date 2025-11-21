package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.util.NetworkResult

class SearchProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(query: String): NetworkResult<List<Product>> {
        if (query.isBlank()) return NetworkResult.Success(emptyList())
        return repository.searchProducts(query)
    }
}