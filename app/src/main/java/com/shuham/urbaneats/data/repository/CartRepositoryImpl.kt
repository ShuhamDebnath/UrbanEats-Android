package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.local.dao.CartDao
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.data.remote.dto.OrderItemDto
import com.shuham.urbaneats.data.remote.dto.OrderRequest
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.CartRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CartRepositoryImpl(
    private val client: HttpClient,
    private val dao: CartDao,
    private val tokenManager: TokenManager
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItemEntity>> {
        return dao.getCartItems()
    }

    override suspend fun addToCart(product: Product) {
        val existingItem = dao.getCartItemById(product.id)
        if (existingItem != null) {
            // Item exists, just increase quantity
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            dao.insertCartItem(updatedItem)
        } else {
            // New item
            dao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    quantity = 1
                )
            )
        }
    }

    override suspend fun removeFromCart(productId: String) {
        dao.deleteCartItem(productId)
    }

    override suspend fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItem(productId)
        } else {
            val existingItem = dao.getCartItemById(productId)
            existingItem?.let {
                dao.insertCartItem(it.copy(quantity = newQuantity))
            }
        }
    }


    override suspend fun placeOrder(
        address: String,
        total: Double,
        items: List<CartItemEntity>
    ): NetworkResult<Unit> {
        return try {

            // 1. Get Real User ID
            val userSession = tokenManager.getUserSession().first()

            if (userSession.id == null) {
                return NetworkResult.Error("User not logged in")
            }


            // 2. Create Request
            val request = OrderRequest(
                userId = userSession.id, // REAL ID
                userName = userSession.name ?: "Unknown", // REAL NAME
                items = items.map { OrderItemDto(it.productId, it.name, it.quantity, it.price) },
                totalAmount = total,
                address = address
            )

            // 3. Send to API
            val response = client.post("api/orders") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                // 4. CRITICAL: Clear Local Cart on Success
                dao.clearCart()
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Order Failed: ${response.status}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.message}")
        }
    }
}
