package com.shuham.urbaneats.data.local.dao

import androidx.room.*
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Read all items (Reactive)
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    // Get specific item (to check if it exists)
    @Query("SELECT * FROM cart_items WHERE productId = :id")
    suspend fun getCartItemById(id: String): CartItemEntity?

    // Insert or Replace
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    // Delete specific item
    @Query("DELETE FROM cart_items WHERE productId = :id")
    suspend fun deleteCartItem(id: String)

    // Clear cart (after checkout)
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}