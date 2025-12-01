package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.util.NetworkResult

class AddUpdateProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product, isEdit: Boolean): NetworkResult<Unit> {
        return if (isEdit) {
            repository.updateProduct(product.id, product)
        } else {
            repository.addProduct(product)
        }
    }
}