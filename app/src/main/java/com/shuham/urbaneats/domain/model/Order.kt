package com.shuham.urbaneats.domain.model

import com.shuham.urbaneats.data.remote.dto.OrderItemDto

data class Order(
    val id: String,
    val items: List<OrderItemDto>, // We can reuse the DTO for inner items for simplicity
    val total: Double,
    val status: String,
    val date: String,
    val address: String
)