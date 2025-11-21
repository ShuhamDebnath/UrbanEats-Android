package com.shuham.urbaneats.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProductDto(
    @SerialName("_id") val id: String, // MongoDB uses "_id", we map it to "id"
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val category: String
)