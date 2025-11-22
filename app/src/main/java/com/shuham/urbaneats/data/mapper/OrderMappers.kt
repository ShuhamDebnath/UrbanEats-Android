package com.shuham.urbaneats.data.mapper

import com.shuham.urbaneats.data.remote.dto.OrderResponseDto
import com.shuham.urbaneats.domain.model.Order

fun OrderResponseDto.toDomain(): Order {
    return Order(
        id = id,
        items = items,
        total = totalAmount,
        status = status,
        date = date.take(10), // Simple hack to get YYYY-MM-DD
        address = address
    )
}