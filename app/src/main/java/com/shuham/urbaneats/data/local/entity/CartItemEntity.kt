package com.shuham.urbaneats.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: String, // Same ID as the Product
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int
)