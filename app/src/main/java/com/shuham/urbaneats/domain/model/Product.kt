package com.shuham.urbaneats.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val category: String,
    val isFavorite: Boolean = false
)