package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.util.NetworkResult

interface CategoryRepository {
    suspend fun getCategories(): NetworkResult<List<Category>>
}