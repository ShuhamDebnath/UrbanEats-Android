package com.shuham.urbaneats.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderResponseDto(
    @SerialName("_id") val id: String,
    val userId: String,
    val userName: String,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val address: String,
    val status: String,
    val date: String // ISO Date String from MongoDB
)