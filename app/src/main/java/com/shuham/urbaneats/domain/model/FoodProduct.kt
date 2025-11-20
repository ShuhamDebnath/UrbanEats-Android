package com.shuham.urbaneats.domain.model

data class FoodProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val rating: Double = 4.5
)