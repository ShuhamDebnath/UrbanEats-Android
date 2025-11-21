package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.util.NetworkResult

class RefreshMenuUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        return repository.refreshProducts()
    }
}