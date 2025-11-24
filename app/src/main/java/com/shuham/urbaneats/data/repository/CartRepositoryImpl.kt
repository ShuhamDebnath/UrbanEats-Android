package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.local.dao.CartDao
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.data.remote.dto.OrderItemDto
import com.shuham.urbaneats.data.remote.dto.OrderRequest
import com.shuham.urbaneats.data.remote.dto.OrderResponseDto
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.CartRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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

    override suspend fun addToCart(product: Product, quantity: Int, options: String, instructions: String) {
        val existingItem = dao.getCartItemById(product.id)

        if (existingItem != null) {
            // Update existing item (adding new quantity to old)
            // Note: In a real app, if options differ, you'd add a separate row.
            // For simplicity here, we just update the quantity and overwrite notes.
            dao.insertCartItem(
                existingItem.copy(
                    quantity = existingItem.quantity + quantity,
                    selectedOptions = options,
                    instructions = instructions
                )
            )
        } else {
            // Insert New
            dao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    name = product.name,
                    price = product.price, // Ideally store the calculated total unit price here
                    imageUrl = product.imageUrl,
                    quantity = quantity,
                    selectedOptions = options,
                    instructions = instructions
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


    override suspend fun placeOrder(address: String, total: Double, items: List<CartItemEntity>): NetworkResult<String> {
        return try {
            val session = tokenManager.getUserSession().first()
            if (session.id == null) return NetworkResult.Error("User not logged in")

            val request = OrderRequest(
                userId = session.id,
                userName = session.name ?: "Valued Customer",
                items = items.map { OrderItemDto(it.productId, it.name, it.quantity, it.price) },
                totalAmount = total,
                address = address
            )

            val response = client.post("api/orders") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                // 1. Parse the Response to get the ID
                val responseData = response.body<OrderResponseDto>()

                // 2. Clear Cart only after success
                dao.clearCart()

                // 3. Return the ID
                NetworkResult.Success(responseData.id)
            } else {
                NetworkResult.Error("Order Failed: ${response.status}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network Error: ${e.localizedMessage}")
        }
    }

    override suspend fun clearCart() {
        dao.clearCart()
    }
}
