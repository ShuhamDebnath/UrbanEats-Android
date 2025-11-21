package com.shuham.urbaneats.data.mapper

import com.shuham.urbaneats.data.local.entity.ProductEntity
import com.shuham.urbaneats.data.remote.dto.ProductDto
import com.shuham.urbaneats.domain.model.Product

// 1. Network -> Database
fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        category = category
    )
}

// 2. Database -> Domain
fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        category = category
    )
}

// 3. Network -> Domain (The Missing Link for Search)
fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        category = category
    )
}