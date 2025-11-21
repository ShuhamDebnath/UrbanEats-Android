package com.shuham.urbaneats.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val userId: String,
    val userName: String,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val address: String
)

@Serializable
data class OrderItemDto(
    val productId: String,
    val name: String,
    val quantity: Int,
    val price: Double
)