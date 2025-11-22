package com.shuham.urbaneats.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// This creates a table named "products" in your phone's storage
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false) // We use the MongoDB ID as the primary key
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val category: String,
    val isFavorite: Boolean = false
)