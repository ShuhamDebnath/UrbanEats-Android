package com.shuham.urbaneats.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.SizeOption

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: String, // Same ID as the Product
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
    val selectedOptions: String = "", // e.g., "Large, Extra Cheese"
    val instructions: String = ""     // e.g., "No onions"
)