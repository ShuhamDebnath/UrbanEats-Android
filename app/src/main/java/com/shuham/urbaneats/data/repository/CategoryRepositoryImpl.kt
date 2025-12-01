package com.shuham.urbaneats.data.repository

import android.util.Log
import com.shuham.urbaneats.data.remote.dto.CategoryDto
import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.domain.repository.CategoryRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable


// Create a simple request object (or use Map)
@Serializable
data class CategoryRequest(val name: String, val imageUrl: String)
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

    override suspend fun addCategory(name: String, base64Image: String): NetworkResult<Unit> {
        return try {
            val formattedImage = if (base64Image.startsWith("http")) base64Image else "data:image/jpeg;base64,$base64Image"

            Log.d("TAG", "addCategory: name $name , base64Image $base64Image")


            val response = client.post("api/categories") {
                contentType(ContentType.Application.Json)
                setBody(CategoryRequest(name, formattedImage))
            }
            if (response.status == HttpStatusCode.OK) NetworkResult.Success(Unit)
            else NetworkResult.Error("Failed to add category")
        } catch (e: Exception) { NetworkResult.Error(e.message) }
    }

    override suspend fun updateCategory(id: String, name: String, base64Image: String): NetworkResult<Unit> {
        return try {
            val formattedImage = if (base64Image.startsWith("http")) base64Image else "data:image/jpeg;base64,$base64Image"

            val response = client.put("api/categories/$id") {
                contentType(ContentType.Application.Json)
                setBody(CategoryRequest(name, formattedImage))
            }
            if (response.status == HttpStatusCode.OK) NetworkResult.Success(Unit)
            else NetworkResult.Error("Failed to update category")
        } catch (e: Exception) { NetworkResult.Error(e.message) }
    }

    override suspend fun deleteCategory(id: String): NetworkResult<Unit> {
        return try {
            val response = client.delete("api/categories/$id")
            if (response.status == HttpStatusCode.OK) NetworkResult.Success(Unit)
            else NetworkResult.Error("Failed to delete category")
        } catch (e: Exception) { NetworkResult.Error(e.message) }
    }


}