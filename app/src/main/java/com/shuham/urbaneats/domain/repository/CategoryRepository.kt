package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.util.NetworkResult

interface CategoryRepository {
    suspend fun getCategories(): NetworkResult<List<Category>>
    // NEW METHODS
    suspend fun addCategory(name: String, base64Image: String): NetworkResult<Unit>
    suspend fun updateCategory(id: String, name: String, base64Image: String): NetworkResult<Unit>
    suspend fun deleteCategory(id: String): NetworkResult<Unit>
}
