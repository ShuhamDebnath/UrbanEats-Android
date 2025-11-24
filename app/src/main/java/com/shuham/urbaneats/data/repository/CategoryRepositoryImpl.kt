package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.remote.dto.CategoryDto
import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.domain.repository.CategoryRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class CategoryRepositoryImpl(private val client: HttpClient) : CategoryRepository {
    override suspend fun getCategories(): NetworkResult<List<Category>> {
        return try {
            val response = client.get("api/categories")
            if (response.status == HttpStatusCode.OK) {
                val dtos = response.body<List<CategoryDto>>()
                NetworkResult.Success(dtos.map { Category(it.id, it.name, it.imageUrl) })
            } else {
                NetworkResult.Error("Failed to load categories")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.message}")
        }
    }
}