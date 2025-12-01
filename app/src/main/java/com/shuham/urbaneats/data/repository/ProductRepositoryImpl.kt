package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.dao.ProductDao
import com.shuham.urbaneats.data.mapper.toDomain
import com.shuham.urbaneats.data.mapper.toEntity
import com.shuham.urbaneats.data.remote.dto.CreateProductRequest
import com.shuham.urbaneats.data.remote.dto.ProductDto
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val client: HttpClient,
    private val dao: ProductDao
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> {
        return dao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // THE FIX: Merge Strategy
    override suspend fun refreshProducts(): NetworkResult<Unit> {
        return try {
            // 1. Fetch fresh data from API
            val response = client.get("api/products")

            if (response.status == HttpStatusCode.OK) {
                val remoteData = response.body<List<ProductDto>>()

                // 2. Get current favorites from Local DB (to preserve them)
                val favoriteIds = dao.getFavoriteProductIds().toSet()

                // 3. Map API data to Entities, KEEPING the favorite status
                val entities = remoteData.map { dto ->
                    val isFav = favoriteIds.contains(dto.id)
                    dto.toEntity(isFavorite = isFav)
                }

                // 4. Insert the merged list
                dao.insertAll(entities)

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
                parameter("q", query)
            }
            if (response.status == HttpStatusCode.OK) {
                val dtos = response.body<List<ProductDto>>()
                // Note: For search results, we could also check against DB for favorites if we wanted perfection
                NetworkResult.Success(dtos.map { it.toDomain() })
            } else {
                NetworkResult.Error("Search failed")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message)
        }
    }

    override suspend fun toggleFavorite(productId: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(productId, isFavorite)
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return dao.getFavoriteProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteProduct(productId: String): NetworkResult<Unit> {
        return try {
            val response = client.delete("api/products/$productId")
            if (response.status == HttpStatusCode.OK) {
                // Optimistic Local Delete (optional, or wait for refresh)
                // But strictly, we should refresh list from server to be safe
                refreshProducts()
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Delete failed")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message)
        }
    }

    override suspend fun addProduct(product: Product): NetworkResult<Unit> {
        return try {
            // Map Domain Product to Request DTO (Ignoring ID since it's new)
            val request = CreateProductRequest(
                name = product.name,
                description = product.description,
                price = product.price,
                imageUrl = product.imageUrl,
                category = product.category,
                sizes = product.sizes,
                addons = product.addons
            )

            val response = client.post("api/products") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                refreshProducts() // Sync DB
                NetworkResult.Success(Unit)
            } else {
                //NetworkResult.Error("Add failed")
                NetworkResult.Error("Add failed: ${response.status}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message)
        }
    }



    override suspend fun updateProduct(id: String, product: Product): NetworkResult<Unit> {
        return try {
            // Map Domain to Update DTO
            // Note: We pass the image URL/Base64 string directly. The ViewModel handles conversion.
            val request = com.shuham.urbaneats.data.remote.dto.UpdateProductRequest(
                name = product.name,
                description = product.description,
                price = product.price,
                imageUrl = product.imageUrl,
                category = product.category,
                sizes = product.sizes,
                addons = product.addons
            )

            val response = client.put("api/products/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                refreshProducts() // Sync DB
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Update failed: ${response.status}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message)
        }
    }
}