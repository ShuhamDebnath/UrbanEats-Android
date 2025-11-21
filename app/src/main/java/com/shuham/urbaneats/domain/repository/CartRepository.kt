package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItemEntity>>
    suspend fun addToCart(product: Product)
    suspend fun removeFromCart(productId: String)
    suspend fun updateQuantity(productId: String, newQuantity: Int)
}