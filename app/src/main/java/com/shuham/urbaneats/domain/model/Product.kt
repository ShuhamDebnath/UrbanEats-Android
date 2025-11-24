package com.shuham.urbaneats.domain.model

import kotlinx.serialization.Serializable

@Serializable // Needed for TypeConverter
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val category: String,
    val isFavorite: Boolean = false,
    // NEW FIELDS
    val sizes: List<SizeOption> = emptyList(),
    val addons: List<AddonOption> = emptyList()
)