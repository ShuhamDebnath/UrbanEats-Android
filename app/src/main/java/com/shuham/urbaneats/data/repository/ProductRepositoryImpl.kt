package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.dao.ProductDao
import com.shuham.urbaneats.data.mapper.toDomain
import com.shuham.urbaneats.data.mapper.toEntity
import com.shuham.urbaneats.data.remote.dto.ProductDto
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val client: HttpClient,
    private val dao: ProductDao
) : ProductRepository {

    // 1. Source of Truth: ALWAYS read from the DB
    override fun getProducts(): Flow<List<Product>> {
        return dao.getAllProducts().map { entities ->
            entities.map { it.toDomain() } // Convert DB entities to Clean Domain models
        }
    }

    // 2. The Sync Logic
    override suspend fun refreshProducts(): NetworkResult<Unit> {
        return try {
            // A. Fetch from API
            val response = client.get("api/products")

            if (response.status == HttpStatusCode.OK) {
                val remoteData = response.body<List<ProductDto>>()

                // B. Save to DB (This automatically triggers the Flow above!)
                dao.insertAll(remoteData.map { it.toEntity() })

                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Server Error: ${response.status}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Fetch failed: ${e.message}")
        }
    }

    override suspend fun getProductById(id: String): Product? {
        return dao.getProductById(id)?.toDomain()
    }

    override suspend fun searchProducts(query: String): NetworkResult<List<Product>> {
        return try {
            val response = client.get("api/products/search") {
                parameter("q", query) // Adds ?q=query to URL
            }
            if (response.status == HttpStatusCode.OK) {
                val dtos = response.body<List<ProductDto>>()
                NetworkResult.Success(dtos.map { it.toDomain() })
            } else {
                NetworkResult.Error("Search failed")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message)
        }
    }

}