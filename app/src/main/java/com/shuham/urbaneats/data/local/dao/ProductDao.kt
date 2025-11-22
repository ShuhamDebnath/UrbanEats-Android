package com.shuham.urbaneats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuham.urbaneats.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // 1. Insert (Update if ID exists)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    // 2. Read All (Returns a Flow for real-time updates)
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    // 3. Delete All (For refresh)
    @Query("DELETE FROM products")
    suspend fun clearAll()

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    // NEW: Get list of IDs that are currently favorites (for merging)
    @Query("SELECT id FROM products WHERE isFavorite = 1")
    suspend fun getFavoriteProductIds(): List<String>

    // Get single product
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?
}