package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetMenuUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getProducts()
    }
}