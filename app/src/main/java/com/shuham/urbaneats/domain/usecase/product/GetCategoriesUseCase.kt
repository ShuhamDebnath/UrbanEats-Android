package com.shuham.urbaneats.domain.usecase.product

import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.domain.repository.CategoryRepository
import com.shuham.urbaneats.util.NetworkResult

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): NetworkResult<List<Category>> {
        return repository.getCategories()
    }
}