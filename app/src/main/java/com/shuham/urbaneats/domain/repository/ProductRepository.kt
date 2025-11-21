package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.util.NetworkResult
import com.shuham.urbaneats.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    // Returns a Flow because the DB can update anytime
    fun getProducts(): Flow<List<Product>>

    // Trigger a refresh from the network
    suspend fun refreshProducts(): NetworkResult<Unit>

    suspend fun getProductById(id: String): Product?

    suspend fun searchProducts(query: String): NetworkResult<List<Product>>
}