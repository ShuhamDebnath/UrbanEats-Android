package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.dao.CartDao
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class CartRepositoryImpl(private val dao: CartDao) : CartRepository {

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
}